(ns om_strokes.alphabet
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
  (atom {:things []}))

(strokes/bootstrap)

; 26 characters in a vec
(def alphabet (vec "abcdefghijklmnopqrstuwvxyz"))

(defn update [svg data]
 ; DATA JOIN
 ; Join new data with old elements, if any.
 (let [text (-> svg (.selectAll "text") (.data data))]
 ; UPDATE
 ; Update old elements as needed
   (-> text (.attr {:class "update"}))

 ; ENTER
 ; Create new elments as needed
   (-> text (.enter) (.append "text")
       (.attr {:class "enter"
               :x     #(* %2 32)
               :dy    ".35em"}))

 ; ENTER + UPDATE
 ; Appending to the enter selection expands the update selection to include
 ; entering elements; so, operations on the update selection after appending to
 ; the enter selection will apply to both entering and updating nodes.
   (-> text (.text identity))

 ; EXIT
 ; Remove old elements as needed.
   (-> text (.exit) (.remove))))

(defn rand-text []
 (-> alphabet
    shuffle
    (subvec (rand-int 26))
    sort
    vec))

(defn set-rand-text-timer [owner]
  (.setTimeout js/window
        (fn []
          (om/set-state! owner :text (rand-text))
          (set-rand-text-timer owner))
        2000))

(defn alphabet-app [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:width 960 :height 500 :text alphabet})
    om/IWillMount
    (will-mount [_]
      (set-rand-text-timer owner))
    om/IRender
    (render [_]
      (dom/div nil (dom/div #js {:id "main"})))
    om/IDidMount
    (did-mount [_]
      (let [{:keys [width height]} (om/get-state owner)
            svg (-> d3
                    (.select "#main")
                    (.append "svg")
                    (.attr {:width width :height height})
                    (.append "g")
                    (.attr {:transform (str "translate(32," (/ height 2) ")")}))]
            (om/update-state! owner #(assoc % :svg svg))))
    om/IDidUpdate
    (did-update [_ prev-snapshot prev-state]
      (let [{:keys [svg text]} (om/get-state owner)]
        (update svg text)))
    ))

(om/root alphabet-app app-state {:target (.getElementById js/document "content")})
