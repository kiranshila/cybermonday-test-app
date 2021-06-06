(ns main.core
  (:require
   ["@dracula/dracula-ui" :as drac]
   [cybermonday.core :as cm]
   [cybermonday.ir :as ir]
   [reagent.core :as r]
   [cljs.pprint :refer [pprint]]
   ["react-highlight" :default Highlight]
   [reagent.dom :as rdom]))

(def parse-result (r/atom nil))
(def ast-format (r/atom false))

(def format-to-type {false "IR"
                     true "HTML"})
(def format-to-parser {false ir/md-to-ir
                       true #(:body (cm/parse-md %))})

(defn parse-content []
  (reset! parse-result ((format-to-parser @ast-format) (.-value (js/document.getElementById "md-area")))))

(defn app []
  [:div
   [:> drac/Heading {:size "2xl"
                     :align "center"
                     :color "purpleCyan"}
    "Cybermonday Markdown to Hiccup Tester"]
   [:> drac/Divider {:color "green"}]
   [:div.flex-container
    [:div.flex-child
     [:> drac/Heading {:size "xl"
                       :align "center"
                       :color "pink"}
      "Markdown"]
     [:textarea.drac-text-white.drac-bg-black-secondary
      {:id "md-area"
       :style {:font-family "Fira Code"
               :border "none"
               :width "100%"
               :height "100%"
               :box-sizing "border-box"}
       :on-input parse-content}]]
    [:div.flex-child
     [:> drac/Heading {:size "xl"
                       :align "center"
                       :color "pink"}
      "Parse Result"]
     [:> Highlight {:language "clojure"} (with-out-str (pprint @parse-result))]
     [:> drac/Box
      [:> drac/Heading "AST Format"]
      [:> drac/Switch {:color "purple"
                       :id "format"
                       :name "format"
                       :on-change (fn []
                                    (reset! ast-format (.-checked (js/document.getElementById "format")))
                                    (parse-content))
                       :default-checked false}]

      [:label {:for "format"
               :class "drac-text drac-text-white"}
       (format-to-type @ast-format)]]]]])

(defn ^:dev/after-load start []
  (rdom/render [app] (js/document.getElementById "app")))

(defn init []
  (start))

(defn ^:dev/before-load stop [])
