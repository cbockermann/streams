(ns stream.Statistics
  (:gen-class
    :state state)
  )

(defn -init []
  [[] {}])
  
(defn add-value [this k v]
  (if (contains? (.state this) k)
    (+ (get (.state this) k) v)
    v))
  
(defn -add [st]
  (let [ks (keySet st)]
    



