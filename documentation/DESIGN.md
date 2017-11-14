# Design Document
For every camera connected we have a passive object, a monitor, for storing valuable information such as current mode (I. E. idle, motion), every frame received from the camera and whether the displaying images should be asynchronous or synchronous.If the modes is changed, the monitor sends messages to mailbox-thread which in return broadcasts it to all monitors connected to the camera. Frames (images) are then passed (from the monitor) to the synchronization thread. It calculates when each frame should be displayed in the GUI and then send it to another monitor. This monitor stores all received frames from all synchronization threads. It then passes frames to another “mega”-synchronization thread which checks if it’s possible to synchronize all the images from the cameras (if synchronization mode is active). We also have four threads active for each camera (two attached to the camera and two attached to the client), which is our sockets. It will handle all the input and output. Lastly we have the GUI that receives frames (from the “mega”- synchronization thread) to display. It will also post button-event messages to the mailbox thread, which will broadcast it to the monitors.


<object data="img/design.pdf" type="application/pdf" width="700px" height="700px">
    <embed src="img/design.pdf">
        Design Illustration: <a href="img/design.pdf">Download PDF</a>.</p>
    </embed>
</object>