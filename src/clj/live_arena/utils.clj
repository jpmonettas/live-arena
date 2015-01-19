(ns live-arena.utils
  (:require [clojure.inspector :refer :all]))

(defn string->int [str] (Integer/parseInt str))

(defn incs [n] (if n (inc n) 1))

(def i inspect-tree)

(def opposite-flag {:red :blue
                    :blue :red})

(def flag-key {"RED" :red
               "BLUE" :blue} )
