(ns live-arena.stats-test
  (:require [live-arena.stats :refer :all]
            [midje.sweet :refer :all]))


(fact "should calculate teams-stats from game"
      (teams-stats {:players-stats {"German" {:team :red
                                              :captured-flags 2}
                                    "JP" {:team :blue
                                          :captured-flags 2}
                                    "Gustav" {:team :red
                                              :captured-flags 1}
                                    "Tincho" {:team :blue
                                              :captured-flags 3}}})

      =>

      {:blue {:members ["JP" "Tincho"]
              :flags 5}
       :red {:members ["German" "Gustav"]
             :flags 3}}) 

(fact "should calculate kills by players from weapons kill"

      (kills-totals {"Diego" {:shotgun 1 :nail 2}
                    "Gustav" {:nail 1}})

      =>
      
      {"Gustav" 1, "Diego" 3})


(fact "should calculate player frag stats for a kills-totals map"

      (player-frags "JP" {"Gustav" 1, "Diego" 3})

      =>

      {["JP" "Gustav"] 1, ["JP" "Diego"] 3})


(fact "should calculate frags battles for a game"
      (frag-battles {:players-stats {"German" {:kills {"Diego" {:shotgun 1 :nail 2}
                                                       "Gustav" {:nail 1}}}
                                     "Gustav" {:kills {"Diego" {:shotgun 1 :nail 2}}}}})

      =>

      {["German" "Diego"] 3
       ["German" "Gustav"] 1
       ["Gustav" "Diego"] 3})
