(ns live-arena.core-test
  (:require [midje.sweet :refer :all]
            [live-arena.core :refer :all]
            [live-arena.event :as e]))


(facts "About general state stepping"

       (fact "we should correcly move games to history when they are shutdown an create new ones"

             (step

              {:current-game {:status :running}
               :games-history [{:end-time "1:00"
                                :status :shutdown}
                               {:end-time "20:00"
                                :status :shutdown}]}

              (e/map->ShutdownGameEvent {:timestamp "30:00"}))

             =>

                 
             {:current-game {}
               :games-history [{:end-time "1:00"
                                :status :shutdown}
                               {:end-time "20:00"
                                :status :shutdown}
                               {:end-time "30:00"
                                :status :shutdown}]}

             ))
