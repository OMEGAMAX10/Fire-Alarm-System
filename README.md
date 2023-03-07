# Fire-Alarm-System
Fire alarm system that detects fire in a camera stream and sends alerts as notifications to the phones of the users that subscribed to this alerting service and are at most 2 km away from the fire.
The engine on which the app is based is Firebase, since we have user authentication, cloud messaging for receiving the alerts, and a real-time database in which all the events are stored.
The detection algorithm is YOLOv8, developed by Ultralytics, and it is capable of detecting fire and smoke.
