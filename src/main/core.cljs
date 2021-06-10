(ns main.core
  (:require
   ["@dracula/dracula-ui" :as drac]
   [cybermonday.core :as cm]
   [cybermonday.ir :as ir]
   [reagent.core :as r]
   [cljs.pprint :refer [pprint]]
   ["react-highlight" :default Highlight]
   [reagent.dom :as rdom]
   ["@matejmazur/react-katex" :as TeX]))

(defonce ast-format (r/atom false))
(defonce md-source (r/atom ""))

(def format-to-type {false "Cybermonday IR"
                     true "HTML"})

(defn lower-inline-math [[_ _ math]]
  [:> TeX {:math math
           :class "drac-text-white"}])

(defn lower-fenced-code-block [[_ {:keys [language]} code]]
  (if (or (= language "math")
          (= language "latex")
          (= language "tex"))
    [:> TeX {:class "drac-text-white"
             :block true} code]
    [:> Highlight {:language language} code]))

(def format-to-parser {false ir/md-to-ir
                       true #(:body (cm/parse-md % {:lower-fns {:markdown/inline-math lower-inline-math
                                                                :markdown/fenced-code-block lower-fenced-code-block}
                                                    :default-attrs {:hr {:class "drac-divider drac-border-orange"}
                                                                    :div {:class ["drac-text-white"]}
                                                                    :p {:class ["drac-text drac-line-height drac-text-white"]}
                                                                    :a {:class ["drac-anchor"
                                                                                "drac-text"
                                                                                "drac-text-purple"
                                                                                "drac-text-pink--hover"
                                                                                "drac-mb-sm"]}
                                                                    :table {:class ["drac-table drac-table-cyan"]
                                                                            :align "center"
                                                                            :style {:margin "auto"
                                                                                    :width "auto"}}
                                                                    :th {:class ["drac-text" "drac-text-white"]}}}))})

(defn parse-content []
  (reset! md-source (.-value (js/document.getElementById "md-area"))))

(defn app []
  [:div {:style {:height "80%"}}
   [:> drac/Heading {:size "2xl"
                     :align "center"
                     :color "purpleCyan"}
    "Cybermonday Test Application"]
   [:div {:align "center"}
    [:> drac/Anchor {:size "lg"
                     :href "https://github.com/kiranshila/cybermonday"}
     "Check out the project on GitHub"]]
   [:> drac/Divider {:color "green"}]
   [:div.flex-container {:style {:height "100%"}}
    [:div.flex-child
     [:> drac/Heading {:size "xl"
                       :align "center"
                       :color "pink"}
      "Markdown"]
     [:textarea.drac-text-white.drac-bg-black-secondary
      {:id "md-area"
       :style {:resize "none"
               :font-family "Fira Code"
               :border "none"
               :width "100%"
               :height "100%"
               :box-sizing "border-box"}
       :on-input parse-content}]]
    [:div.flex-child
     [:> drac/Heading {:size "xl"
                       :align "center"
                       :color "pink"}
      "Syntax Tree"]
     [:> Highlight {:language "clojure"} (with-out-str (pprint ((format-to-parser @ast-format) @md-source)))]
     [:> drac/Box
      [:div {:align "center"}
       [:> drac/Heading "Format"]
       [:> drac/Switch {:color "purple"
                        :id "format"
                        :name "format"
                        :on-change (fn []
                                     (reset! ast-format (.-checked (js/document.getElementById "format"))))
                        :default-checked false}]

       [:label {:for "format"
                :class "drac-text drac-text-white"}
        (format-to-type @ast-format)]]]]
    [:div.flex-child.drac-box
     [:> drac/Heading {:size "xl"
                       :align "center"
                       :color "pink"}
      "Rendered Result"]
     ((format-to-parser true) @md-source)]]])

(defn ^:dev/after-load start []
  (rdom/render [app] (js/document.getElementById "app")))

(defn init []
  (start))

(defn ^:dev/before-load stop [])
