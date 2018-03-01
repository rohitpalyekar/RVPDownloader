# RVPDownloader
It is Downloading application which uses RXjava 2 and Okhttp
The user enters the Url in Editview and starts the download.
In the Service the RXjava 2 observable is invoked, the Fetchdata runs the okHttp sync call on a new Thread (Schedulers).
Once the Task is completed the Observer which has subscrided the Observable emitts data in onNext.

I have also included a Custom Notification so that user is made aware once the downloading is completed.




RxJava 2 is one of the real extraordinary libaries. 
Still Learning.
Hope you liked it.
