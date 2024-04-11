## AnkiConverter
Simple program that converts media to a smaller format and sends it to Anki
### Features
- Convert audio to AAC
- Convert image to WEBP
- Insert media into a specific field on a card (by tag)
- Configuration
### Required
- [Anki-Connect](https://ankiweb.net/shared/info/2055492159)
- [FFmpeg](https://ffmpeg.org/)
- [ShareX](https://getsharex.com/)

## How to use
After first run, `ankiConverter.properties` file will be generated. Configure program in this file. Then, in ShareX, configure action in `task settings` -> `actions` (*you can put it in a separate task `hotkey settings` -> `your task settings` -> `actions`*).
In the action settings, specify path to the program and the following arguments:
- "-webp" "$input" (for the task of saving screenshot)
- "-aac" "$input" (for the task of recording audio)
 
The app will then automatically convert and send media files to Anki. Unconverted files are automatically deleted
