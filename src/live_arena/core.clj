(ns live-arena.core
  (:require [clojure.core.match :as m]
            [clojure.string :as s]
            [live-arena.player :as p])
  (:gen-class))

(comment
  {:overall-stats (games-merge-here)
   :current-game {"JP" {:team :red
                        :kills {"Pixel" {:railgun 10
                                         :rocket 5}}
                        :points 0
                        :awards {"DEFENCE" 1
                                 "EXCELENT" 3}
                        :captured-flags 3
                        :returned-flags 2}}
   :games-history [{game1}
                   {game2}
                   {game3}]})


(defprotocol Event

  (enhance [this])

  (step-game [this game]))

(defrecord InitGameEvent [timestamp]
  Event

  (enhance [e] e)
  
  (step-game [_ game]
    (update-in game [:status] {nil :running})))

(defrecord CaptureLimitEvent [timestamp]
  Event

  (enhance [e] e)
  
  (step-game [this game]
    (-> game
      (update-in [:status] (constantly :finished))
      (update-in [:finish-reason] (constantly :capture-limit)))))

(defrecord TimeLimitEvent [timestamp]
  Event

  (enhance [e] e)
  
  (step-game [this game]
    (-> game
      (update-in [:status] (constantly :finished))
      (update-in [:finish-reason] (constantly :time-limit)))))


(defrecord AwardEvent [timestamp player award]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))

(defrecord KillEvent [timestamp victim killer weapon]
  Event

  (enhance [e]
    (-> e
      (update-in [:weapon] {"MOD_FALLING" :falling
                            "MOD_RAILGUN" :railgun
                            "MOD_MACHINEGUN" :machinegun
                            "MOD_CHAINGUN" :chaingun
                            "MOD_SHOTGUN" :shotgun
                            "MOD_SUICIDE" :suicide
                            "MOD_GRENADE_SPLASH" :grenade
                            "MOD_PLASMA" :plasma
                            "MOD_PLASMA_SPLASH" :plasma
                            "MOD_ROCKET" :rocket
                            "MOD_GAUNTLET" :gauntlet
                            "MOD_NAIL" :nail
                            "MOD_ROCKET_SPLASH" :rocket
                            "MOD_LIGHTNING" :lightning
                            "MOD_GRENADE" :grenade
                            "MOD_KAMIKAZE" :kamikaze
                            "MOD_PROXIMITY_MINE" :proximity-mine})
      (update-in [:victim] p/nick->id)
      (update-in [:killer] p/nick->id)))
  
  (step-game [this game] game))

(defrecord PlayerScoreEvent [timestamp player points]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))

(defrecord ShutdownGameEvent [timestap]
  Event

  (enhance [e] e)
  
  (step-game [this game] game))

(defrecord FlagGotEvent [timestap player flag]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))

(defrecord FlagCaptureEvent [timestap player flag]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))

(defrecord FlagCarrierFraggedEvent [timestap player flag]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))

(defrecord FlagReturnedEvent [timestap player flag]
  Event

  (enhance [e]
    (update-in e [:player] p/nick->id))
  
  (step-game [this game] game))



(defn build-event [line]
  (let [line-formats [[#"..(.*?) InitGame: .*" ->InitGameEvent]
                      [#"..(.*?) Award: .*?: (.*?) gained the (.*?) award!" ->AwardEvent]
                      [#"..(.*?) Kill: .*?: (.*?) killed (.*?) by (.*?)" ->KillEvent [:timestamp :victim :killer :weapon]]
                      [#"..(.*?) PlayerScore: .*?: (.*?) now has (.*?) points" ->PlayerScoreEvent]
                      [#"..(.*?) ShutdownGame:.*" ->ShutdownGameEvent]
                      [#"..(.*?) CTF: .*?: (.*?) got the (RED|BLUE) flag!" ->FlagGotEvent]
                      [#"..(.*?) CTF: .*?: (.*?) captured the (RED|BLUE) flag!" ->FlagCaptureEvent]
                      [#"..(.*?) CTF: .*?: (.*?) fragged (RED|BLUE)'s flag carrier!" ->FlagCarrierFraggedEvent]
                      [#"..(.*?) CTF: .*?: (.*?) returned the (RED|BLUE) flag!" ->FlagReturnedEvent]
                      [#"..(.*?) Exit: Capturelimit hit.*" ->CaptureLimitEvent]
                      [#"..(.*?) Exit: Timelimit hit.*" ->TimeLimitEvent]]]
    (loop [formats line-formats]
      (let [[regexp constructor attrs] (first formats)
            match (when regexp (re-matches regexp line))]
        (if match
          (apply constructor (rest match))
          (when (next formats) (recur (next formats))))))))


#_(slurp "./logs/one-ctf-game.log")
#_(build-event "5:08 Award: 7 3: Assassin gained the DEFENCE award!")
#_(->> (slurp "./logs/one-ctf-game.log")
    (s/split-lines)
    (map build-event)
    (remove nil?)
    (map enhance)
    (take 10)
    (map prn)
    (doall))
