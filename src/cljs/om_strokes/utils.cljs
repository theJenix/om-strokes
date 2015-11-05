(ns om-strokes.utils
  (:require [cljs.reader :as reader])
  (:import [goog.ui IdGenerator]))

(defn guid []
  (.getNextUniqueId (.getInstance IdGenerator)))
