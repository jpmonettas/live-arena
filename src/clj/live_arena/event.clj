(ns live-arena.event
  (:require [clojure.string :as s]
            [live-arena.player :as p]
            [live-arena.utils :as u]))


(defprotocol Event

  (enhance [this])

  (step-game [this game]))

(defrecord InitGameEvent [timestamp]
  Event

  (enhance [this] this)
  
  (step-game [this game]
    (-> game
      (assoc :start-time timestamp)
      (update-in [:status] {nil :running}))))

(defrecord CaptureLimitEvent [timestamp]
  Event

  (enhance [this] this)
  
  (step-game [this game]
    (-> game
      (update-in [:status] (constantly :finished))
      (update-in [:finish-reason] (constantly :capture-limit)))))

(defrecord TimeLimitEvent [timestamp]
  Event

  (enhance [this] this)
  
  (step-game [this game]
    (-> game
      (update-in [:status] (constantly :finished))
      (update-in [:finish-reason] (constantly :time-limit)))))

(defrecord ShutdownGameEvent [timestamp]
  Event

  (enhance [this] this)
  
  (step-game [this game]
    (-> game
      (assoc :end-time timestamp)
      (update-in [:status] (constantly :shutdown)))))

(defrecord AwardEvent [timestamp player award]
  Event

  (enhance [this]
    (update-in this [:player] p/nick->id))
  
  (step-game [this game]
    (update-in game [:players-stats player :awards award] u/incs)))
 
(defrecord KillEvent [timestamp victim killer weapon]
  Event

  (enhance [this]
    (-> this
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
  
  (step-game [this game]
    (let [killer-team (get-in game [:players-stats killer :team])
          victim-team (get-in game [:players-stats victim :team])]
      (-> game
        (update-in [:players-stats killer :kills victim weapon] u/incs)
        (assoc-in [:players-stats killer :team] (or killer-team (u/opposite-flag victim-team)))
        (assoc-in [:players-stats victim :team] (or victim-team (u/opposite-flag killer-team)))))))

(defrecord PlayerScoreEvent [timestamp player points]
  Event

  (enhance [this]
    (-> this
      (update-in [:player] p/nick->id)
      (update-in [:points] u/string->int)))
  
  (step-game [this game]
    (update-in game [:players-stats player :points-hist] (fn [ph] (conj ph points)))))



(defrecord FlagGotEvent [timestap player flag]
  Event

  (enhance [this]
    (-> this
      (update-in [:player] p/nick->id)
      (update-in [:flag] u/flag-key)))
  
  (step-game [this game]
    (assoc-in game [:players-stats player :team] (u/opposite-flag flag))))

(defrecord FlagCaptureEvent [timestap player flag]
  Event

  (enhance [this]
    (-> this
      (update-in [:player] p/nick->id)
      (update-in [:flag] u/flag-key)))
  
  (step-game [this game]
    (-> game
      (update-in [:players-stats player :captured-flags] u/incs)
      (assoc-in [:players-stats player :team] (u/opposite-flag flag)))))

(defrecord FlagCarrierFraggedEvent [timestap player flag]
  Event

  (enhance [this]
    (-> this
      (update-in [:player] p/nick->id)
      (update-in [:flag] u/flag-key)))
  
  (step-game [this game]
    (assoc-in game [:players-stats player :team] (u/opposite-flag flag))))

(defrecord FlagReturnedEvent [timestap player flag]
  Event

  (enhance [this]
    (-> this
      (update-in [:player] p/nick->id)
      (update-in [:flag] u/flag-key)))
  
  (step-game [this game]
    (-> game
      (update-in [:players-stats player :returned-flags] u/incs)
      (assoc-in [:players-stats player :team] flag))))


(defn build-event [line]
  (let [line-formats [[#"(.*?) InitGame: .*" ->InitGameEvent]
                      [#"(.*?) Award: .*?: (.*?) gained the (.*?) award!" ->AwardEvent]
                      [#"(.*?) Kill: .*?: (.*?) killed (.*?) by (.*?)" ->KillEvent [:timestamp :victim :killer :weapon]]
                      [#"(.*?) PlayerScore: .*?: (.*?) now has (.*?) points" ->PlayerScoreEvent]
                      [#"(.*?) ShutdownGame:.*" ->ShutdownGameEvent]
                      [#"(.*?) CTF: .*?: (.*?) got the (RED|BLUE) flag!" ->FlagGotEvent]
                      [#"(.*?) CTF: .*?: (.*?) captured the (RED|BLUE) flag!" ->FlagCaptureEvent]
                      [#"(.*?) CTF: .*?: (.*?) fragged (RED|BLUE)'s flag carrier!" ->FlagCarrierFraggedEvent]
                      [#"(.*?) CTF: .*?: (.*?) returned the (RED|BLUE) flag!" ->FlagReturnedEvent]
                      [#"(.*?) Exit: Capturelimit hit.*" ->CaptureLimitEvent]
                      [#"(.*?) Exit: Timelimit hit.*" ->TimeLimitEvent]]]
    (loop [formats line-formats]
      (let [[regexp constructor attrs] (first formats)
            match (when regexp (re-matches regexp line))]
        (if match
          (enhance (apply constructor (rest match)))
          (when (next formats) (recur (next formats))))))))
