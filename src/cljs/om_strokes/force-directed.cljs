(ns om_strokes.force-directed
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [cljs-http.client :as http]
              [strokes :refer [d3]]
              [om-strokes.utils]))

;; Lets you do (prn "stuff") to the console
(enable-console-print!)

(def app-state
  (atom {:nodes [{:name "A" :index 0}
                 {:name "B" :index 1}
                 {:name "C" :index 2}
                 {:name "D" :index 3}]
         :links [{:target 1 :source 0 :index 0 :weight 1.0}
                 {:target 2 :source 0 :index 1 :weight 1.0}
                 {:target 3 :source 1 :index 2 :weight 1.0}]}))

(strokes/bootstrap)

(defn -setup-force-layout [fl graph]
  (.. fl
      (nodes (.-nodes graph))
      (links (.-links graph))
      start))

(defn -build-links [svg graph]
  (.. svg
      (selectAll ".link")
      (data (.-links graph))
      enter
      (append "line")
      (attr "class" "link")
      (attr "stroke" "grey")
      (style "stroke-width" 1)))

(defn -build-nodes [svg graph]
  (.. svg
      (selectAll ".node")
      (data (.-nodes graph))
      enter
      (append "text")
      (attr "cx" 12)
      (attr "cy" ".35em")
      (text #(.-name %))
      ))
 
(defn -start-graph [svg fl]
  (.. svg (call (.-drag fl))))

(defn -on-tick [link node]
  (fn []
    (.. link
        (attr "x1" #(.. % -source -x))
        (attr "y1" #(.. % -source -y))
        (attr "x2" #(.. % -target -x))
        (attr "y2" #(.. % -target -y)))
    (.. node
        (attr "transform" #(str "translate(" (.. % -x) "," (.. % -y) ")")))))

(defn force-directed-app [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:width 960 :height 600 :graph  app})
    om/IWillMount
    (will-mount [_])
    om/IRender
    (render [_]
      (dom/div nil (dom/div #js {:id "main"})))
    om/IDidMount
    (did-mount [_]
      (let [{:keys [width height graph]} (om/get-state owner)
            json (clj->js graph)
            ;; build-force-layout
            fl  (.. d3
                    -layout
                    force
                    (charge -140)
                    (linkDistance 40)
                    (size (array width height)))
            ;; build-svg
            svg (-> d3
                    (.select "#main")
                    (.append "svg")
                    (.attr {:width width :height height}))
            links (-build-links svg json)
            nodes (-> (-build-nodes svg json)
                      (-start-graph fl))]
        (-setup-force-layout fl json)
        ;; (-start-graph svg fl)
        (.on fl "tick"
             (-on-tick links nodes))
        (om/update-state! owner #(assoc % :svg svg :fl fl))))))

(om/root force-directed-app app-state {:target (.getElementById js/document "content")})
