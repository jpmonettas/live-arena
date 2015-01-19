(ns live-arena.core
  (:require [clojure.string :as s]
            [live-arena.event :as e]
            [live-arena.stats :as stats]
            [live-arena.utils :as u]
            [clojure.core.reducers :as r])
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


(defn combine [state1 state2]
  {:current-game {}
   :games-history (concat (:games-history state1) (:games-history state2))
   :overall-stats {:frag-battles (merge-with + (-> state1 :overall-stats :frag-battles)
                                             (-> state2 :overall-stats :frag-battles))
                   :match-battles (merge-with + (-> state1 :overall-stats :match-battles)
                                              (-> state2 :overall-stats :match-battles))}})

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

  ;; Simple one
  (time (->> (slurp "./logs/two-ctf-game.log")
          (s/split-lines)
          (pmap e/build-event)
          (remove nil?)
          (reduce step {})))

  ;; First performance optimization
  (time (->> (slurp "./logs/two-ctf-game.log")
          (s/split-lines)
          (pmap e/build-event)
          (remove nil?)
          (partition-by #(instance? live_arena.event.ShutdownGameEvent %))))

  ;; Second performance optimization 
  (time (->> (slurp "/home/jmonetta/games.log")
                          (s/split-lines)
                          (pmap e/build-event)
                          (remove nil?)
                          (partition-by #(instance? live_arena.event.ShutdownGameEvent %))
                          (partition 20)
                          (reduce concat)
                          (pmap (partial reduce step {}))
                          (reduce combine {})))

  


  )

