;(ns stream.clojure)
;  (:gen-class))

;(defn -process [this data]
;  (clojure.print data))

(ns example.Printer
  (:gen-class
   :implements [stream.Processor]))

(defn -process [this item]
  (let [data (java.util.LinkedHashMap. item)]
    (doseq (.put data "key" "value")
      data)
;    
;  (stream.data.DataFactory/create (java.util.LinkedHashMap. (into item "key" "value"))))
;  (println (str "data item is: " item)))