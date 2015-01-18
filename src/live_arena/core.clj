(ns live-arena.core
  (:require [clojure.string :as s]
            [live-arena.event :as e]
            [live-arena.stats :as stats]
            [live-arena.utils :as u])
  (:gen-class))

(comment
  
  {:overall-stats {:frag-battles {["JP" "Pixel"] 115
                                  ["Pixel" "JP"] 220}
                   :match-battles {["JP" "German"] 5
                                   ["Pixel" "JP"] 4}}
   :current-game {:players-stats {"JP" {:team :red
                                        :kills {"Pixel" {:railgun 10
                                                         :rocket 5}}
                                        :points 0
                                        :awards {"DEFENCE" 1
                                                 "EXCELENT" 3}
                                        :captured-flags 3
                                        :returned-flags 2}}
                  :status :running}
   :games-history [{}
                   {}
                   {}]})



(defn step [state e]
  (let [stepped-game (e/step-game e (:current-game state))]
    (if (= (:status stepped-game) :shutdown)
      ;; a game is over, move it to history, add it to stats and
      ;; create a new one
      (-> state
        (update-in [:games-history] (fn [hist] (conj hist stepped-game)))
        (update-in [:overall-stats] stats/update-stats stepped-game)
        (assoc :current-game {}))

      ;; just keep stepping the current game
      (assoc state :current-game stepped-game))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; For trying on the repl
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment

  (e/build-event "5:08 Award: 7 3: Assassin gained the DEFENCE award!")

  (def two-state (->> (slurp "./logs/two-ctf-game.log")
                   (s/split-lines)
                   (map e/build-event)
                   (remove nil?)
                   (reduce step {})))

  (def g1 (-> two-state :games-history first))


  )

