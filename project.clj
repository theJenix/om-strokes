(defproject om-strokes "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.reader "0.8.8"]
                 ;; CLJ
                 [ring/ring-core "1.3.1"]
                 [compojure "1.1.9"]
                 [cheshire "5.3.1"]
                 ;; CLJS
                 [org.clojure/clojurescript "0.0-2322"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [cljs-http "0.1.16"]
                 [secretary "1.2.1"]
                 [net.drib/strokes "0.5.1"]
                 [om "0.7.3"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.7"]
            [lein-pdo "0.1.1"]]

  :aliases {"dev" ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]}

  :ring {:handler om-strokes.core/app
         :init    om-strokes.core/init}

  :source-paths ["src/clj"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/gen/om_strokes.js"
                                   :output-dir "resources/public/js/gen"
                                   :optimizations :none
                                   :source-map true
                                   :externs ["react/externs/react.js"]}}
                       {:id "release"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/gen/om_strokes.js"
                                   :source-map "resources/public/js/gen/om_strokes.js.map"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :output-wrapper false
                                   :preamble ["react/react.min.js"]
                                   :externs ["react/externs/react.js"]
                                   :closure-warnings
                                   {:non-standard-jsdoc :off}}}]})
