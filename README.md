## AnkiConverter
Simple program that converts media to a smaller format and sends it to Anki

*Also try [AutoFFsubsync](https://github.com/Sgale952/AutoFFsubsync)*
### Features
- Convert audio to AAC
- Convert image to WEBP
- Insert media into a specific field on a card
- Translate glossary with DeepL
- Configurable
### Required
*You can disable unnecessary modules and get rid of dependencies*
- [Anki-Connect](https://ankiweb.net/shared/info/2055492159) - to work with anki
- [ShareX](https://getsharex.com/) - to automatically start the program
- [FFmpeg](https://ffmpeg.org/) - to convert audio into aac
- [DeepL API key](https://www.deepl.com/pro-api?cta=header-pro-api) - to translate the glossary

## How to use
1. Download [release](https://github.com/Sgale952/AnkiConverter/releases)
2. After first run, `ankiConverter.properties` file will be generated. Configure program in this file
3. Then, in ShareX, configure action in `task settings` -> `actions`
   (*you can put it in a separate task `hotkey settings` -> `your task settings` -> `actions`*)
4. In the action settings, specify path to the program and the argument `"$input"`
 
The app will then automatically convert and send media files to Anki. Unconverted files are automatically deleted
***
### [More info](https://github.com/Sgale952/AnkiConverter/wiki)
