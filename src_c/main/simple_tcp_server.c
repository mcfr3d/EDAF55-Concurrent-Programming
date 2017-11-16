#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <string.h>
#include "camera.h"

static int bind_server_socket(int fd, int port);

/*
 * create a server socket bound to port
 * and listening.
 *  
 * return positive file descriptor 
 * or negative value on error
 */
int create_server_socket(int port)
{
    int fd = -1;
    
    if(port < 0 || port > 65535) {
       errno = EINVAL;
       return -1;
    }
    fd = socket(AF_INET,SOCK_STREAM,0);
    if(fd < 0) return fd;
    if(bind_server_socket(fd,port) < 0) return -1;

    if(listen(fd,10)) return -1;

    return fd;
}

static int bind_server_socket(int fd, int port){

    struct sockaddr_in addr;
    int val = 1;
    if(setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val))) {
        perror("setsockopt");
        return -1;
    }
    
    /* see man page ip(7) */
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

    if(bind(fd, (struct sockaddr*) &addr, sizeof(addr))) return -1;

#ifdef INFO
    printf("simple_tcp_server: bound fd %d to port %d\n",fd,port);
#endif
    
    return fd;
}

/*
 * Serve one client: send image
 */
/*static int do_serve2(int fd, int pic)
{
    int clientfd;

    printf("Attempting accept on fd %d\n",fd);
    if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
    printf("Client connected from address...\n");

    //Get Picture Size
    printf("Getting Picture Size\n");
    const char* filePath;
    if(pic % 2 == 0) {
        filePath = "./meme_image1.jpg";
    } else {
        filePath = "./meme_image2.jpg";
    }
    FILE *picture;
    picture = fopen(filePath, "r");
    if(picture == NULL) {
        printf("Failed to get picture\n");
        printf("Closing socket.\n");
        return close(clientfd);
    }
    printf("Picture fetched.\n");
    int size;
    fseek(picture, 0, SEEK_END);
    size = ftell(picture);
    fseek(picture, 0, SEEK_SET);

    //Send Picture Size
    printf("Sending Picture Size\n");
    printf("Picture size (%d) bytes\n",size);
    write(clientfd, &size, sizeof(size));

    //Send Picture as Byte Array
    printf("Sending Picture as Byte Array\n");
    char send_buffer[size];
    while(!feof(picture)) {
        fread(send_buffer, 1, sizeof(send_buffer), picture);
        write(clientfd, send_buffer, sizeof(send_buffer));
        bzero(send_buffer, sizeof(send_buffer));
    }
     error:
        printf("Closing clientfd (%d)\n",clientfd);
    return close(clientfd);
}*/

static int do_serve(int fd, camera* cam)
{
    int clientfd;

    printf("Attempting accept on fd %d\n",fd);
    if((clientfd = accept(fd, NULL, NULL)) < 0) return -1;
    printf("Client connected.\n");

    frame *f = camera_get_frame(cam);
    size_t size = get_frame_size(f);
    byte *bytes = get_frame_bytes(f);

    printf("Sending Picture Size\n");
    write(clientfd, &size, sizeof(size));

    printf("Sending picture.\n");
    write(clientfd, bytes, size);

    frame_free(f);
     error:
        printf("Closing clientfd (%d)\n",clientfd);
    return close(clientfd);
}

int main()
{
    int fd = create_server_socket(5000);

    if(fd < 0){
        perror("create_server_socket");
        return 1;
    }
    int a;
    for(a = 0; a<10; a++) {
        int i = 0;
        int n = 247;
        camera* cam = camera_open();
        printf("Opening camera...\n");
        do {
            printf("Wating for clients to connect...\n");
            do_serve(fd, cam);
            i++;
        }
        while(i < n);
        camera_close(cam);
    }


    printf("Closing socket: %d\n", fd);
    close(fd);

    return 0;
}
