const express = require('express');
const app = express();
const fs = require('fs');
const mysql = require('mysql');
const mediaserver = require('mediaserver');


//connection a la base de données qui contient la table song 
var con = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "",
    database: "streamapp"
  });

// serveur a l'ecoute sur le port 3000
app.listen('3000', () => {
    console.log('listen on 3000...');
});

/**  Route qui declenche le streaming audio **/
app.get('/music/:id', (req, res) => {
  
   var id = req.params.id;
   
    con.query('SELECT * FROM song WHERE id = ?', [id], function (err, result) {
        
      if (err) throw err;

        mediaserver.pipe(req,res,result[0].lien);
     
    });

});


// pour accéder a la chanson suivante dans la liste
app.get('/next',(req,res)=>{
    
    var current_music = req.query.current ;

    var id = parseInt(current_music) + 1 ;
  
    con.query('SELECT id, titre , chanteur , album FROM song WHERE id = ?', [id], function (err, result) {
        
        if (err)  throw err;
          
      if(result.length > 0 ){
        
        res.send(result[0]);
                
      }else{

        con.query('SELECT id, titre , chanteur , album FROM song WHERE id = ?', [1], function (err, result) {
        
          if (err) throw err;
         
          res.send(result[0]);
                  
        });
      }
        
    });

});

// pour accéder a la chanson précédante
app.get('/previous', (req,res)=>{
  
    var current_music = req.query.current ;

    var id = parseInt(current_music) - 1 ;
   
    con.query('SELECT id, titre , chanteur , album FROM song WHERE id = ?', [id], function (err, result) {
        
        if (err) throw err;
       
        if(result.length > 0 ){
        
            res.send(result[0]);
                    
        }else{
    
            con.query('SELECT id, titre , chanteur , album FROM song ORDER BY id desc limit 1', function (err, result) {
        
              if (err) throw err; 
              res.send(result[0]);
                      
            });
          
        }       
    
    });
});


// pour recuperer tout les chansons  a partir de la base de donnée 
app.get('/all', (req,res)=>{

      con.query('SELECT * FROM song',  function (err, result) {

        if (err) throw err;
        //console.log(result);
        res.send(result);
      
      });
  
});

// pour récupérer les infos d'une chanson a travers son titre
app.get('/getSongUrl', (req, res) => {
    
    var song = req.query.identifiant ;
    song = song.replace(/_/g, ' ');
    console.log(song);

    con.query('SELECT id, titre , chanteur , album FROM song WHERE titre = ? ', [song], function (err, result) {
        
        if (err) throw err;

      if(result.length > 0 ){
        
         console.log(result[0]);
         res.send(result[0]);
         
      }else{

            var empty = {id : -1 , titre : 'NOT FOUND' , chanteur: 'NOT FOUND', album: 'NOT FOUND'};
            res.send(empty);
        
      }
        
    });
  
});