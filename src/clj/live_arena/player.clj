(ns live-arena.player)

(def nicknames
  {"Diego" #{"Diego"}

   "German" #{"German Infantry"
             "The Red Machine"
             "The Machine"
             "I dnt like this shit"
             "No fear. German here"
             "sudo bash root/root"
             "No Mercy"
             "Certified PHP Expert"
             "Murmaider"
             "Railgunning for fun"} 
   
   "Cherta" #{"cherta"
             "Obi Wan"
             "Gorostiaga"
             "Gorostiaga Pelado"} 
   
   "Pixel" #{"Snipper reloaded"
            "Cabo Narancio"} 
   
   "JP" #{"dolphin"
         "Juan sin tierra"
         "homeless"} 
   
   "Dani" #{"UnnamedPlayer"} 
   
   "Nando" #{"EL DOLOR ES PASAJERO"
            "RAY CHARLES"
            "MENDOZAAAAAA"
            "BERNARDO"
            "NANDO"
            "MMBRGRBRMBGR"
            "A LA BALSA!!!"
            "ESTOESUNAPORONG"
            "EL PATO BOMBARDIER"
            "CORAZON DE PATO"
            "PATO VICIOSO"
            "PATO ATRAGANTADO"
            "PATO CABRERO"
            "PATO VERANIEGO"
            "PATO SANGUINARIO"} 
   
   "Tincho" #{"Tincho"
             "VOLVIO UN DIA"
             "EL TENEDOR MAGICO"
             "EL VASCO"
             "RONDON"
             "PITUFINA Y RONALDO"
             "LA PITUFINA IS BACK"
             "EL CAMPERA"
             "BOMBERO SIN BOINA"
             "CABEZA SALAZAR"
             "YO NO HAGO DIETA"
             "TEODORO"
             "PARO AEREO NOMAS!!"
             "EL FLAUTA BARREIRO"
             "PITUFINA IS BACK"
             "SIMBAD EL MARINO SOY"
             "SIMBAD EL MARINO"
             "CROHN"
             "CROHN IS BACK"
             "CRHIS DEDUM"
             "EXTRANIO A PITUFINA"
             "EL BODEGUITA"
             "VASECTOMY GUY"}
   

   
   "Rodrigo" #{"rocko"
              "Rockosaurio"
              "Tetano"} 
   
   "Gustav" #{"At MAdrid Campeon"
             "General McArthur"
             "La Anchoa de Medero"
             "Matrero"
             "Insecticide"
             "Bauxite Rules!"
             "si jp mina le doy"
             "Franklin Tseng"
             "Somo Subro"} 
   
   "Bot" #{"Arachna"
          "Andriy"
          "Gargoyle"
          "Assassin"
          "Ayumi"
          "Angelyss"
          "Beret"
          "Sergei"
          "Broadklin"
          "Cyber-Garg"
          "Metalbot"
          "S_Marine"
          "Sorceress"
          "Dark"
          "Liz"
          "Kyonshi"
          "Jenna"
          "Grunt"
          "Skelebot"
          "Grism"
          "Headcrash"} 
   
   "Fede" #{"V Neck"}})

(defn id->nick [id]
  (first (nicknames id)))

(defn nick->id [nick]
  (reduce (fn [rname [name nicks]]
            (if (nicks nick)
              name
              rname))
          nil
          nicknames))
