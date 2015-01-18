(ns live-arena.stats
  (:require [clojure.math.combinatorics :as comb]))


(defn teams-stats

  "Given a game returns a map with teams as keys and for each team
  data like members and captured flags"
  
  [game]
  
  (let [tms (->> game
              :players-stats
              vec
              (group-by (comp :team second)))]
    (reduce
     (fn [r [team plyrs-data]]
       (merge r {team {:members (map first plyrs-data)
                       :flags (->> plyrs-data
                                (map (comp :captured-flags second))
                                (remove nil?)
                                (reduce + 0))}}))
     {}
     tms)))

(defn winners-and-loosers

  "Returns the team that wons and the teams that lost the game"
  
  [game]
  (let [[[_ winners] [_ loosers]] (->> game
                            teams-stats
                            vec
                            (sort-by (comp :flags second) >))]
    {:winners winners :loosers loosers}))

(defn kills-totals
  
  "Given a player kill map by weapon, returns a map with totals by player
  instead of weapons"
  
  [kills-map]
  
  (reduce (fn [r [name weapons-map]]
            (assoc r name (apply + (vals weapons-map))))
          {}
          kills-map))

(defn player-frags

  "Given a name and it's total kills returns a map of all its frags
  against each of his opponents
  Like : {[JP German] 4
          [JP Gustav] 3
          [German JP] 8}"

  [name kills]
  
  (reduce (fn [r [enemy t]]
            (assoc r [name enemy] t))
   {}
   kills))

(defn frag-battles
  "Returns the same as player-frags but for an entire game"
  [game]
  
  (reduce (fn [r [name {:keys [kills]}]]
            (merge r (player-frags name (kills-totals kills))))
          {}
          (:players-stats game)))

(defn match-battles

  [game]
  
  (let [{:keys [winners loosers]} (winners-and-loosers game)]
    (->> (comb/cartesian-product (-> winners :members) (-> loosers :members))
      (reduce (fn [r [pl1 pl2]] (assoc r [pl1 pl2] 1)) {}))))

(defn update-stats [stats game]
  (-> stats
    (update-in [:frag-battles] (partial merge-with + (frag-battles game)))
    (update-in [:match-battles] (partial merge-with + (match-battles game)))))
