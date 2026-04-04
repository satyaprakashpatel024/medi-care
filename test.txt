-- Table order and constraints may not be valid for execution.

CREATE TABLE public.address (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  address_line1 character varying NOT NULL,
  address_line2 character varying,
  address_type character varying,
  city character varying NOT NULL,
  country character varying NOT NULL,
  created_at timestamp without time zone NOT NULL,
  is_default boolean NOT NULL,
  landmark character varying,
  phone_number character varying,
  postal_code character varying NOT NULL,
  state character varying NOT NULL,
  updated_at timestamp without time zone,
  user_id integer,
  CONSTRAINT address_pkey PRIMARY KEY (id),
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.appointments (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  appointment_date timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  diagnosis character varying,
  notes text,
  status character varying NOT NULL,
  treatment character varying,
  department_id integer,
  doctor_id integer NOT NULL,
  patient_id integer NOT NULL,
  prescription_id integer UNIQUE,
  CONSTRAINT appointments_pkey PRIMARY KEY (id),
  CONSTRAINT fk_appt_department FOREIGN KEY (department_id) REFERENCES public.departments(id),
  CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id) REFERENCES public.doctors(id),
  CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES public.patients(id),
  CONSTRAINT fk_appt_prescription FOREIGN KEY (prescription_id) REFERENCES public.prescription(id)
);
CREATE TABLE public.departments (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  name character varying NOT NULL UNIQUE,
  CONSTRAINT departments_pkey PRIMARY KEY (id)
);
CREATE TABLE public.doctors (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  blood_type character varying,
  created_at timestamp without time zone NOT NULL,
  date_of_birth date,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying,
  last_name character varying NOT NULL,
  phone character varying,
  speciality character varying NOT NULL,
  updated_at timestamp without time zone,
  department_id integer,
  hospital_id integer,
  user_id integer NOT NULL UNIQUE,
  CONSTRAINT doctors_pkey PRIMARY KEY (id),
  CONSTRAINT fk_doctor_department FOREIGN KEY (department_id) REFERENCES public.departments(id),
  CONSTRAINT fk_doctor_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id),
  CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.hospital_departments (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  department_id integer NOT NULL,
  hospital_id integer NOT NULL,
  CONSTRAINT hospital_departments_pkey PRIMARY KEY (id),
  CONSTRAINT fk_hd_department FOREIGN KEY (department_id) REFERENCES public.departments(id),
  CONSTRAINT fk_hd_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id)
);
CREATE TABLE public.hospitals (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  name character varying NOT NULL,
  phone character varying,
  address_id integer UNIQUE,
  CONSTRAINT hospitals_pkey PRIMARY KEY (id),
  CONSTRAINT fk_hospital_address FOREIGN KEY (address_id) REFERENCES public.address(id)
);
CREATE TABLE public.otp_tables (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp without time zone NOT NULL,
  email character varying NOT NULL,
  expired_at timestamp without time zone NOT NULL,
  otp character varying NOT NULL,
  CONSTRAINT otp_tables_pkey PRIMARY KEY (id)
);
CREATE TABLE public.patients (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  blood_type character varying,
  created_at timestamp without time zone NOT NULL,
  date_of_birth date,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying,
  last_name character varying NOT NULL,
  phone character varying,
  updated_at timestamp without time zone,
  user_id integer NOT NULL UNIQUE,
  CONSTRAINT patients_pkey PRIMARY KEY (id),
  CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.prescription (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp without time zone NOT NULL,
  dosage_instructions text,
  medications text NOT NULL,
  notes text,
  doctor_id integer NOT NULL,
  patient_id integer NOT NULL,
  CONSTRAINT prescription_pkey PRIMARY KEY (id),
  CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES public.doctors(id),
  CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES public.patients(id)
);
CREATE TABLE public.staffs (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  blood_type character varying,
  created_at timestamp without time zone NOT NULL,
  date_of_birth date,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying,
  last_name character varying NOT NULL,
  phone character varying,
  updated_at timestamp without time zone,
  hospital_id integer,
  user_id integer NOT NULL UNIQUE,
  CONSTRAINT staffs_pkey PRIMARY KEY (id),
  CONSTRAINT fk_staff_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id),
  CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.users (
  id integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp without time zone NOT NULL,
  email character varying NOT NULL UNIQUE,
  is_active boolean NOT NULL,
  last_login timestamp without time zone,
  password_hash character varying NOT NULL,
  role character varying NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);