(ns live-arena.core
  (:require [live-arena.event :as e]
            [clojure.string :as s]
            [live-arena.utils :as u])
  (:gen-class))

;; {:overall-stats (games-merge-here)
;;  :current-game {:players-stats {"JP" {:team :red
;;                                       :kills {"Pixel" {:railgun 10
;;                                                        :rocket 5}}
;;                                       :dies {"German" {:railgun 8}}
;;                                       :points 0
;;                                       :awards {"DEFENCE" 1
;;                                                "EXCELENT" 3}
;;                                       :captured-flags 3
;;                                       :returned-flags 2}}
;;                 :status running}
;;  :games-history [{game1}
;;                  {game2}
;;                  {game3}]}





(def initial-game {:overall-stats {}
                   :current-game {}
                   :games-history []})

(defn teams

  "Given a game returns the names of blue an red teams."
  
  [game]
  (let [tms (->> game
              :players-stats
              vec
              (group-by (comp :team second)))]
    {:blue (map first (:blue tms))
     :red (map first (:red tms))}))


(defn step [state e]
  (let [stepped-game (e/step-game e (:current-game state))]
    (if (= (:status stepped-game) :shutdown)
      (-> state
        (update-in [:games-history] (fn [hist] (conj hist stepped-game)))
        (assoc :current-game {}))

      (assoc state :current-game stepped-game))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; For trying on the repl
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#_(e/build-event "5:08 Award: 7 3: Assassin gained the DEFENCE award!")

#_(def one-state (->> (slurp "./logs/one-ctf-game.log")
                   (s/split-lines)
                   (map e/build-event)
                   (remove nil?)
                   (map e/enhance)
                   (reduce step initial-game)))

#_(-> one-state :games-history first teams)

