;(ns stream.clojure)
;  (:gen-class))

;(defn -process [this data]
;  (clojure.print data))

(ns example.Proc
  (:gen-class
   :implements [stream.Processor]))

(defn -process [this item]
  (println (str "data item is: " item)))

(defn -toString [this]
  (println this))

(defn -main [args]
  (println (str "args: " args)))
  

;(compile 'example.Proc)