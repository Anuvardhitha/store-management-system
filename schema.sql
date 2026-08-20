--
-- PostgreSQL database dump
--

\restrict nRXHJplyLO4nbDpActgAoKTBYw1c5NtHlsV8fVYSC3edDYgPQf5zRZYjAaXENWo

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: bill; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bill (
    bill_id integer NOT NULL,
    bill_date timestamp without time zone DEFAULT now() NOT NULL,
    total_amount numeric(10,2) DEFAULT 0 NOT NULL
);


ALTER TABLE public.bill OWNER TO postgres;

--
-- Name: bill_bill_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bill_bill_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bill_bill_id_seq OWNER TO postgres;

--
-- Name: bill_bill_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bill_bill_id_seq OWNED BY public.bill.bill_id;


--
-- Name: bill_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bill_item (
    bill_item_id integer NOT NULL,
    bill_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    subtotal numeric(10,2) NOT NULL
);


ALTER TABLE public.bill_item OWNER TO postgres;

--
-- Name: bill_item_bill_item_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bill_item_bill_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bill_item_bill_item_id_seq OWNER TO postgres;

--
-- Name: bill_item_bill_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bill_item_bill_item_id_seq OWNED BY public.bill_item.bill_item_id;


--
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
    product_id integer NOT NULL,
    name character varying(150) NOT NULL,
    price numeric(10,2) NOT NULL,
    quantity integer NOT NULL,
    min_stock_level integer NOT NULL
);


ALTER TABLE public.product OWNER TO postgres;

--
-- Name: product_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_product_id_seq OWNER TO postgres;

--
-- Name: product_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_product_id_seq OWNED BY public.product.product_id;


--
-- Name: stock_movement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stock_movement (
    movement_id integer NOT NULL,
    product_id integer NOT NULL,
    change_type character varying(20) NOT NULL,
    quantity integer NOT NULL,
    reference_id integer,
    moved_at timestamp without time zone DEFAULT now() NOT NULL,
    note character varying(255)
);


ALTER TABLE public.stock_movement OWNER TO postgres;

--
-- Name: stock_movement_movement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.stock_movement_movement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.stock_movement_movement_id_seq OWNER TO postgres;

--
-- Name: stock_movement_movement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.stock_movement_movement_id_seq OWNED BY public.stock_movement.movement_id;


--
-- Name: bill bill_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill ALTER COLUMN bill_id SET DEFAULT nextval('public.bill_bill_id_seq'::regclass);


--
-- Name: bill_item bill_item_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill_item ALTER COLUMN bill_item_id SET DEFAULT nextval('public.bill_item_bill_item_id_seq'::regclass);


--
-- Name: product product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product ALTER COLUMN product_id SET DEFAULT nextval('public.product_product_id_seq'::regclass);


--
-- Name: stock_movement movement_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stock_movement ALTER COLUMN movement_id SET DEFAULT nextval('public.stock_movement_movement_id_seq'::regclass);


--
-- Name: bill_item bill_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill_item
    ADD CONSTRAINT bill_item_pkey PRIMARY KEY (bill_item_id);


--
-- Name: bill bill_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill
    ADD CONSTRAINT bill_pkey PRIMARY KEY (bill_id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (product_id);


--
-- Name: stock_movement stock_movement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stock_movement
    ADD CONSTRAINT stock_movement_pkey PRIMARY KEY (movement_id);


--
-- Name: bill_item bill_item_bill_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill_item
    ADD CONSTRAINT bill_item_bill_id_fkey FOREIGN KEY (bill_id) REFERENCES public.bill(bill_id);


--
-- Name: bill_item bill_item_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bill_item
    ADD CONSTRAINT bill_item_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(product_id);


--
-- Name: stock_movement stock_movement_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stock_movement
    ADD CONSTRAINT stock_movement_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(product_id);


--
-- PostgreSQL database dump complete
--

\unrestrict nRXHJplyLO4nbDpActgAoKTBYw1c5NtHlsV8fVYSC3edDYgPQf5zRZYjAaXENWo

