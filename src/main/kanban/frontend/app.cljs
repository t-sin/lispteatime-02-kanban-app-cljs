(ns kanban.frontend.app
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

;; (def kanban
;;   {:name "lispteatime #02 準備"
;;    :columns
;;    [;; 列
;;     {:name "TODO"
;;      :tasks
;;      [{:name "ClojureScript入門"
;;        :asignee "t-sin"}]}
;;     {:name "DOING"}]})

(def kanban (r/atom {:name ""}))
(def id-counter (r/atom 0))

(defn edit-kanban [name]
  (swap! kanban assoc :name name))

(defn add-column [name]
  (let [id (swap! id-counter inc)
        column {:name name :tasks {} :input (r/atom "aaaaa")}]
    (swap! kanban assoc-in [:columns id] column)
    id))

(defn edit-column [id name])
(defn remove-column [id])

(defn add-task [col-id name assignee]
  (let [id (swap! id-counter inc)
        task {:name name :assignee assignee}]
    (swap! kanban assoc-in [:columns col-id :tasks id] task)
    id))

(defn edit-task [id name assignee])
(defn remove-task [col-id id]
  (swap! kanban update-in [:columns col-id :tasks] dissoc id))

;; components

(defn task-component [col-id id t]
  [:li
   {:class "task"}
   (:name t)
   " - "
   (:assignee t)
   [:span {:on-click #(remove-task col-id id)}  "🥝"]])

(defn task-list-component [col-id tasks input]
  [:ul
   (for [[id t] (sort-by key tasks)]
     ^{:key id} [task-component col-id id t])
   [:li
    [:input {:type :text
             :value @input
             :placeholder "add task..."
             :on-change #(reset! input (-> % .-target .-value))
             ;;; c = {width: 330}; c.width
             :on-key-down #(case (.-which %)
                             ;; エンターキー押したら
                             13 (add-task col-id @input "t-sin")
                             nil)}]]])
  
(defn column-component [id col]
  [:div
   [:h2 (:name col)]
   [task-list-component id (:tasks col) (:input col)]])

(defn column-list-component [cols]
  [:ul
   (for [[id col] (sort-by key cols)]
     ^{:key id} [:li [column-component id col]])])

(defn kanban-component [kanban]
  [:div
   [:h1 (:name @kanban)]
   [column-list-component (:columns @kanban)]])

(defn init []
  (println "hello cljs")
  (println kanban)
  (edit-kanban "listpteatime #2 準備")
  (println kanban)
  (let [id (add-column "TODO")]
    (add-task id "クロージング" "t-sin")
    (add-task id "アンケート確認" "t-sin"))
  (let [id (add-column "DOING")]
    (add-task id "司会進行" "t-sin")
    (add-task id "ライブコーディング" "t-sin"))
  (let [id (add-column "DONE")]
    (add-task id "目標設定" "t-sin")
    (add-task id "ClojureScript入門" "t-sin"))

  (println kanban)
  
  (rd/render [kanban-component kanban] (js/document.getElementById "reagent-root")))
