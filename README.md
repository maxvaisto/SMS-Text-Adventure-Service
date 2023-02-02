# SMS-Text-Adventure-Service

SMS Text Adventure Bot is an android java application that when running in the background serves as a text messaging service for others. 

The application creates a text message listener that given a valid phone number and message passes it to the command interpriter which respond to the users command via a text message. 
However, this sends a lot of messages made to look like a single message which is why the phone sms limit should be increased manaually.

The application has 61 text adventures ready to be played with in total over 50 MB of text. The stories are from chooseyourstory.com.

User progress is saved into a custom built SQL database operated through a repository and a DAO. 

