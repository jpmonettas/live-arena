(ns live-arena.event-test
  (:require [live-arena.event :refer :all]
            [midje.sweet :refer :all]))

(facts "About creating events and enhacing them"
       
       (fact "should build and enhance a kill event for a kill line"
             (let [e (enhance (build-event "  5:41 Kill: 0 4 23: Matrero killed VOLVIO UN DIA by MOD_NAIL"))]

               e        => {:timestamp "  5:41", :victim "Gustav", :killer "Tincho", :weapon :nail}

               (type e) => live_arena.event.KillEvent)))


(facts "About stepping a game with an event"

       (fact "should assign teams and increase kills and dies"

             (step-game
              
              (map->KillEvent {:timestamp "  5:41", :victim "Gustav", :killer "German", :weapon :nail})

              {:players-stats {"German" {:team nil
                                        :kills {"Diego" {:shotgun 1 :nail 2}
                                                "Gustav" {:nail 1}}
                                        :dies {"Gustav" {:rocket 2}
                                               "Nando" {:railgun 1 :plasma 3}}}
                              "Gustav" {:team :blue
                                        :kills {"Diego" {:shotgun 1 :nail 2}
                                                "Gustav" {:nail 1}}
                                        :dies {"Gustav" {:rocket 2}
                                               "Nando" {:railgun 1 :plasma 3}}}}
               :status :running})

             =>

             {:players-stats {"German" {:team :red
                                        :kills {"Diego" {:shotgun 1 :nail 2}
                                                "Gustav" {:nail 2}}
                                        :dies {"Gustav" {:rocket 2}
                                               "Nando" {:railgun 1 :plasma 3}}}
                              "Gustav" {:team :blue
                                        :kills {"Diego" {:shotgun 1 :nail 2}
                                                "Gustav" {:nail 1}}
                                        :dies {"Gustav" {:rocket 2}
                                               "Nando" {:railgun 1 :plasma 3}
                                               "German" {:nail 1}}}}
              :status :running})




       (fact "should increase captured flags for captured flag events and assign teams"

             (step-game

              (map->FlagCaptureEvent {:timestap "  6:27", :player "Gustav", :flag :red})

              {:players-stats {"Gustav" {:captured-flags 2}}})

             =>

             {:players-stats {"Gustav" {:team :blue
                                        :captured-flags 3}}})
       

       (fact "should add points to points-hist every time someone scores"

             (step-game

              (map->PlayerScoreEvent {:timestamp "  6:27", :player "German", :points 31})

              {:players-stats {"German" {:points-hist [10]}}})

             =>

             {:players-stats {"German" {:points-hist [10 31]}}}))
