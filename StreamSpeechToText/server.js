'use strict';
const express = require('express') ;
const app = express();
const multer = require('multer');
const fs = require('fs');
var request = require('request');
const speech = require('@google-cloud/speech');

app.get('/', (req, res) => {
  res.send('Hello World!')
});

app.listen(3001, () => {
  console.log('listening on 3001!');
});

////////////////////////////////////////////////////////////////////
var Storage =  multer.diskStorage({
  destination : "/audio",
  filename:(req,file,cb)=>{
    cb(null,file.originalname);
  } 
}); 

const upload = multer({
storage:Storage
}).single('file');
////////////////////////////////////////////////////////////////////

//recieve file
app.post('/transcriptPost', upload, (req, res, next)=> {
 
  const fileR = req.file ;
  
  if (!fileR) {
    const error = new Error('Please upload a file')
    error.httpStatusCode = 400
    return next(error)
  }

  const file = fs.readFileSync(fileR.path);
  const audioBytes = file.toString('base64');

  const audio = {
    content: audioBytes,
  };
  
  const config = {
    encoding: 'AMR_WB',
    sampleRateHertz: 16000,
    languageCode: 'fr-FR',
  };

  const Json = {
    audio: audio,
    config: config,
  };

  var options = {
    uri: 'https://speech.googleapis.com/v1/speech:recognize?key=you_key_here',
    method: 'POST',
    json: Json
  };

  request(options, function (error, response, body) {
    if (!error && response.statusCode == 200) {

      res.send(body.results[0].alternatives[0].transcript);

    }
  });
 
});
