CREATE TABLE public.address
(
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at    timestamp with time zone            NOT NULL,
    updated_at    timestamp with time zone            NOT NULL,
    address_line1 character varying                   NOT NULL,
    address_line2 character varying,
    address_type  character varying CHECK (address_type::text = ANY (ARRAY['HOME':: character varying, 'WORK':: character varying]::text[])
) ,
  city character varying NOT NULL,
  country character varying NOT NULL,
  is_default boolean NOT NULL,
  landmark character varying,
  phone_number character varying,
  postal_code character varying NOT NULL,
  state character varying NOT NULL,
  user_id bigint,
  CONSTRAINT address_pkey PRIMARY KEY (id),
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.appointments
(
    id               bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at       timestamp with time zone            NOT NULL,
    updated_at       timestamp with time zone            NOT NULL,
    appointment_date timestamp with time zone            NOT NULL,
    hospital_id      bigint                              NOT NULL,
    notes            text,
    status           character varying                   NOT NULL CHECK (status::text = ANY (ARRAY['SCHEDULED':: character varying, 'COMPLETED':: character varying, 'CANCELLED':: character varying, 'NO_SHOW':: character varying]::text[])
) ,
  treatment character varying,
  department_id bigint,
  doctor_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  CONSTRAINT appointments_pkey PRIMARY KEY (id),
  CONSTRAINT fk_appointment_department FOREIGN KEY (department_id) REFERENCES public.departments(id),
  CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES public.doctors(id),
  CONSTRAINT fk_appointment_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id),
  CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES public.patients(id)
);
CREATE TABLE public.departments
(
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at  timestamp with time zone            NOT NULL,
    updated_at  timestamp with time zone            NOT NULL,
    description character varying,
    name        character varying                   NOT NULL UNIQUE,
    CONSTRAINT departments_pkey PRIMARY KEY (id)
);
CREATE TABLE public.doctors
(
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at  timestamp with time zone            NOT NULL,
    updated_at  timestamp with time zone            NOT NULL,
    blood_group character varying CHECK (blood_group::text = ANY (ARRAY['A_POS':: character varying, 'A_NEG':: character varying, 'B_POS':: character varying, 'B_NEG':: character varying, 'AB_POS':: character varying, 'AB_NEG':: character varying, 'O_POS':: character varying, 'O_NEG':: character varying]::text[])
) ,
  date_of_birth date NOT NULL,
  department_id bigint NOT NULL,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying NOT NULL CHECK (gender::text = ANY (ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'OTHER'::character varying]::text[])),
  hospital_id bigint NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  last_name character varying NOT NULL,
  phone character varying NOT NULL,
  speciality character varying NOT NULL,
  user_id bigint NOT NULL UNIQUE,
  CONSTRAINT doctors_pkey PRIMARY KEY (id),
  CONSTRAINT fk_doctor_department FOREIGN KEY (department_id) REFERENCES public.departments(id),
  CONSTRAINT fk_appointment_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id)
);
CREATE TABLE public.hospital_address
(
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at    timestamp with time zone            NOT NULL,
    updated_at    timestamp with time zone            NOT NULL,
    address_line1 character varying                   NOT NULL,
    address_line2 character varying,
    city          character varying                   NOT NULL,
    country       character varying                   NOT NULL,
    landmark      character varying,
    phone_number  character varying,
    postal_code   character varying                   NOT NULL,
    state         character varying                   NOT NULL,
    hospital_id   bigint                              NOT NULL,
    CONSTRAINT hospital_address_pkey PRIMARY KEY (id),
    CONSTRAINT fk_hosp_addr_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals (id)
);
CREATE TABLE public.hospital_departments
(
    id             bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at     timestamp with time zone            NOT NULL,
    updated_at     timestamp with time zone            NOT NULL,
    active         boolean                             NOT NULL DEFAULT true,
    department_id  bigint                              NOT NULL,
    head_doctor_id bigint UNIQUE,
    hospital_id    bigint                              NOT NULL,
    CONSTRAINT hospital_departments_pkey PRIMARY KEY (id),
    CONSTRAINT fk_hd_department FOREIGN KEY (department_id) REFERENCES public.departments (id),
    CONSTRAINT fk_department_head_doctor FOREIGN KEY (head_doctor_id) REFERENCES public.doctors (id),
    CONSTRAINT fk_hd_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals (id)
);
CREATE TABLE public.hospitals
(
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at timestamp with time zone            NOT NULL,
    updated_at timestamp with time zone            NOT NULL,
    name       character varying                   NOT NULL,
    phone      character varying,
    CONSTRAINT hospitals_pkey PRIMARY KEY (id)
);
CREATE TABLE public.insurances
(
    id                     bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at             timestamp with time zone            NOT NULL,
    updated_at             timestamp with time zone            NOT NULL,
    coverage_amount        double precision                    NOT NULL,
    deductible             double precision,
    expiry_date            date                                NOT NULL,
    policy_number          character varying                   NOT NULL UNIQUE,
    policy_type            character varying                   NOT NULL,
    provider_contact_email character varying,
    provider_name          character varying                   NOT NULL,
    provider_phone_number  character varying,
    start_date             date                                NOT NULL,
    status                 character varying                   NOT NULL CHECK (status::text = ANY (ARRAY['ACTIVE':: character varying, 'EXPIRED':: character varying, 'CANCELLED':: character varying, 'PENDING_VERIFICATION':: character varying]::text[])
) ,
  patient_id bigint,
  CONSTRAINT insurances_pkey PRIMARY KEY (id),
  CONSTRAINT fk_insurance_patient FOREIGN KEY (patient_id) REFERENCES public.patients(id)
);
CREATE TABLE public.medical_records
(
    id              bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at      timestamp with time zone            NOT NULL,
    updated_at      timestamp with time zone            NOT NULL,
    diagnosis       character varying                   NOT NULL,
    follow_up_notes character varying,
    hospital_id     bigint                              NOT NULL,
    record_date     date                                NOT NULL,
    status          character varying                   NOT NULL CHECK (status::text = ANY (ARRAY['ACTIVE':: character varying, 'ARCHIVED':: character varying]::text[])
) ,
  symptoms character varying,
  treatment_plan character varying,
  appointment_id bigint,
  doctor_id bigint NOT NULL,
  patient_id bigint NOT NULL,
  CONSTRAINT medical_records_pkey PRIMARY KEY (id),
  CONSTRAINT fk_medical_record_appointment FOREIGN KEY (appointment_id) REFERENCES public.appointments(id),
  CONSTRAINT fk_medical_record_doctor FOREIGN KEY (doctor_id) REFERENCES public.doctors(id),
  CONSTRAINT fk_medical_record_patient FOREIGN KEY (patient_id) REFERENCES public.patients(id)
);
CREATE TABLE public.otp_tables
(
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at timestamp with time zone            NOT NULL,
    updated_at timestamp with time zone            NOT NULL,
    email      character varying                   NOT NULL,
    expired_at timestamp with time zone            NOT NULL,
    otp        character varying                   NOT NULL,
    CONSTRAINT otp_tables_pkey PRIMARY KEY (id)
);
CREATE TABLE public.patients
(
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at  timestamp with time zone            NOT NULL,
    updated_at  timestamp with time zone            NOT NULL,
    blood_group character varying CHECK (blood_group::text = ANY (ARRAY['A_POS':: character varying, 'A_NEG':: character varying, 'B_POS':: character varying, 'B_NEG':: character varying, 'AB_POS':: character varying, 'AB_NEG':: character varying, 'O_POS':: character varying, 'O_NEG':: character varying]::text[])
) ,
  date_of_birth date,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying CHECK (gender::text = ANY (ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'OTHER'::character varying]::text[])),
  last_name character varying NOT NULL,
  phone character varying,
  hospital_id bigint NOT NULL,
  user_id bigint NOT NULL UNIQUE,
  CONSTRAINT patients_pkey PRIMARY KEY (id),
  CONSTRAINT fk_patient_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id),
  CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.prescription
(
    id                  bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at          timestamp with time zone            NOT NULL,
    updated_at          timestamp with time zone            NOT NULL,
    dosage_instructions text,
    medications         text                                NOT NULL,
    notes               text,
    appointment_id      bigint,
    doctor_id           bigint                              NOT NULL,
    patient_id          bigint                              NOT NULL,
    CONSTRAINT prescription_pkey PRIMARY KEY (id),
    CONSTRAINT fk_prescription_appointment FOREIGN KEY (appointment_id) REFERENCES public.appointments (id),
    CONSTRAINT fk_prescription_doctor FOREIGN KEY (doctor_id) REFERENCES public.doctors (id),
    CONSTRAINT fk_prescription_patient FOREIGN KEY (patient_id) REFERENCES public.patients (id)
);
CREATE TABLE public.staffs
(
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at  timestamp with time zone            NOT NULL,
    updated_at  timestamp with time zone            NOT NULL,
    blood_group character varying CHECK (blood_group::text = ANY (ARRAY['A_POS':: character varying, 'A_NEG':: character varying, 'B_POS':: character varying, 'B_NEG':: character varying, 'AB_POS':: character varying, 'AB_NEG':: character varying, 'O_POS':: character varying, 'O_NEG':: character varying]::text[])
) ,
  date_of_birth date NOT NULL,
  emergency_contact character varying,
  first_name character varying NOT NULL,
  gender character varying CHECK (gender::text = ANY (ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'OTHER'::character varying]::text[])),
  last_name character varying NOT NULL,
  phone character varying,
  hospital_id bigint,
  user_id bigint NOT NULL UNIQUE,
  CONSTRAINT staffs_pkey PRIMARY KEY (id),
  CONSTRAINT fk_staff_hospital FOREIGN KEY (hospital_id) REFERENCES public.hospitals(id),
  CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE TABLE public.users
(
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at    timestamp with time zone            NOT NULL,
    updated_at    timestamp with time zone            NOT NULL,
    email         character varying                   NOT NULL UNIQUE,
    is_active     boolean                             NOT NULL,
    last_login    timestamp with time zone,
    password_hash character varying                   NOT NULL,
    role          character varying                   NOT NULL CHECK (role::text = ANY (ARRAY['PATIENT':: character varying, 'DOCTOR':: character varying, 'STAFF':: character varying, 'ADMIN':: character varying, 'RECEPTIONIST':: character varying, 'GUEST':: character varying, 'OWNER':: character varying]::text[])
) ,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);