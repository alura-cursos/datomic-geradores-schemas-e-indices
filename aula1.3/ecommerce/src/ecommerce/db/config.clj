(ns ecommerce.db.config
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]
            [ecommerce.db.categoria :as db.categoria]
            [ecommerce.db.produto :as db.produto]
            [ecommerce.db.venda :as db.venda]))


(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao! []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco! []
  (d/delete-database db-uri))

(def schema [

             {:db/ident       :produto/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O nome de um produto"}
             {:db/ident       :produto/slug
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O caminho para acessar esse produto via http"}
             {:db/ident       :produto/preco
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "O preço de um produto com precisão monetária"}
             {:db/ident       :produto/palavra-chave
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/many}
             {:db/ident       :produto/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :produto/categoria
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident       :produto/estoque
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one}
             {:db/ident       :produto/digital
              :db/valueType   :db.type/boolean
              :db/cardinality :db.cardinality/one}
             {:db/ident       :produto/variacao
              :db/valueType   :db.type/ref
              :db/isComponent true
              :db/cardinality :db.cardinality/many}
             {:db/ident       :produto/visualizacoes
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/noHistory   true}

             {:db/ident       :variacao/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :variacao/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident       :variacao/preco
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one}

             {:db/ident       :categoria/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident       :categoria/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}

             {:db/ident       :venda/produto
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident       :venda/quantidade
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one}
             {:db/ident       :venda/situacao
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident       :venda/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}

             {:db/ident       :tx-data/ip
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one}

             ])

(defn cria-schema! [conn]
  (d/transact conn schema))

(defn cria-dados-de-exemplo! [conn]
  (def eletronicos (model/nova-categoria "Eletrônicos"))
  (def esporte (model/nova-categoria "Esporte"))
  (db.categoria/adiciona! conn [eletronicos, esporte])

  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.10M, 10))
  (def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 888888.10M))
  (def celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 0.1M))
  (def xadrez (model/novo-produto (model/uuid) "Tabuleiro de xadrez", "/tabuleiro-de-xadrez", 30M, 5))
  (def jogo (assoc (model/novo-produto (model/uuid) "Jogo online", "/jogo-online", 20M) :produto/digital true))
  (db.produto/adiciona-ou-altera! conn [computador, celular, celular-barato, xadrez, jogo] "200.216.222.125")

  (db.categoria/atribui! conn [computador, celular, celular-barato, jogo] eletronicos)
  (db.categoria/atribui! conn [xadrez] esporte)

  (db.venda/adiciona! conn (:produto/id computador) 3)
  (db.venda/adiciona! conn (:produto/id computador) 4)
  (db.venda/adiciona! conn (:produto/id computador) 8))
