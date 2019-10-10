(ns ecommerce.db.venda
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]
            [ecommerce.db.entidade :as db.entidade]))

(defn adiciona!
  [conn produto-id quantidade]
  (let [id (model/uuid)]
    (d/transact conn [{:db/id            "venda"
                       :venda/produto    [:produto/id produto-id]
                       :venda/quantidade quantidade
                       :venda/id         id
                       :venda/situacao   "nova"}])
    id))

(defn instante-da-venda [db venda-id]
  (d/q '[:find ?instante .
         :in $ ?id
         :where [_ :venda/id ?id ?tx true]
         [?tx :db/txInstant ?instante]]
       db venda-id))

; executamos em duas queries, poderíamos executar em uma só
; fazendo junções com nested queries ou novas condições
(defn custo [db venda-id]
  (let [instante (instante-da-venda db venda-id)]
    (d/q '[:find (sum ?preco-por-produto) .
           :in $ ?id
           :where [?venda :venda/id ?id]
           [?venda :venda/quantidade ?quantidade]
           [?venda :venda/produto ?produto]
           [?produto :produto/preco ?preco]
           [(* ?preco ?quantidade) ?preco-por-produto]]
         (d/as-of db instante) venda-id)))

(defn todas-nao-canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]
         [?venda :venda/situacao ?situacao]
         [(not= ?situacao "cancelada")]]
       db))

(defn todas-inclusive-canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]]
       db))


(defn canceladas [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]
         [?venda :venda/situacao "cancelada"]]
       db))

(defn altera-situacao! [conn venda-id situacao]
  (d/transact conn [{:venda/id       venda-id
                     :venda/situacao situacao}]))

(defn cancela! [conn venda-id]
  (altera-situacao! conn venda-id "cancelada"))

(defn historico [db venda-id]
  (->> (d/q '[:find ?instante ?situacao
              :in $ ?id
              :where [?venda :venda/id ?id]
              [?venda :venda/situacao ?situacao ?tx true]
              [?tx :db/txInstant ?instante]]
            (d/history db) venda-id)
       (sort-by first)))



(defn historico-geral [db instante-desde]
  (let [filtrado (d/since db instante-desde)]
    (->> (d/q '[:find ?instante ?situacao ?id
                :in $ $filtrado
                :where [$ ?venda :venda/id ?id]
                [$filtrado ?venda :venda/situacao ?situacao ?tx]
                [$filtrado ?tx :db/txInstant ?instante]]
              db filtrado)
         (sort-by first))))















