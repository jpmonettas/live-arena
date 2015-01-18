(ns live-arena.stats)

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

(defn frag-battles [game]
  (reduce (fn [r [name {:keys [kills]}]]
            (merge r (player-frags name (kills-totals kills))))
          {}
          (:players-stats game)))

(defn update-stats [stats game]
  (-> stats
    (update-in [:frag-battles] (partial merge-with + (frag-battles game)))))
