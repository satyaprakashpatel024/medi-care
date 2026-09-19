-- V1__initial_schema.sql
-- Initial Schema for Medi-Care Application

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  email
  VARCHAR
(
  255
) NOT NULL UNIQUE,
  is_active BOOLEAN NOT NULL,
  last_login TIMESTAMP WITH TIME ZONE,
                         password_hash VARCHAR (255) NOT NULL,
  role VARCHAR
(
  50
) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT users_pkey PRIMARY KEY
(
  id
)
  );

-- 2. Hospitals Table
CREATE TABLE IF NOT EXISTS hospitals
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  name
  VARCHAR
(
  255
) NOT NULL,
  phone VARCHAR
(
  50
),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT hospitals_pkey PRIMARY KEY
(
  id
)
  );

-- 3. Departments Table
CREATE TABLE IF NOT EXISTS departments
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  description
  VARCHAR
(
  500
),
  name VARCHAR
(
  255
) NOT NULL UNIQUE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT departments_pkey PRIMARY KEY
(
  id
)
  );

-- 4. Doctors Table
CREATE TABLE IF NOT EXISTS doctors
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  blood_group
  VARCHAR
(
  20
),
  date_of_birth DATE NOT NULL,
  department_id BIGINT NOT NULL,
  emergency_contact VARCHAR
(
  50
),
  first_name VARCHAR
(
  100
) NOT NULL,
  gender VARCHAR
(
  20
) NOT NULL,
  hospital_id BIGINT NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  last_name VARCHAR
(
  100
) NOT NULL,
  phone VARCHAR
(
  50
) NOT NULL,
  speciality VARCHAR
(
  100
) NOT NULL,
  user_id BIGINT NOT NULL UNIQUE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT doctors_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_doctor_user FOREIGN KEY
(
  user_id
) REFERENCES users
(
  id
),
  CONSTRAINT fk_doctor_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
),
  CONSTRAINT fk_doctor_department FOREIGN KEY
(
  department_id
) REFERENCES departments
(
  id
)
  );

-- 5. Patients Table
CREATE TABLE IF NOT EXISTS patients
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  blood_group
  VARCHAR
(
  20
),
  date_of_birth DATE,
  emergency_contact VARCHAR
(
  50
),
  first_name VARCHAR
(
  100
) NOT NULL,
  gender VARCHAR
(
  20
),
  last_name VARCHAR
(
  100
) NOT NULL,
  phone VARCHAR
(
  50
),
  hospital_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL UNIQUE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT patients_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_patient_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
),
  CONSTRAINT fk_patient_user FOREIGN KEY
(
  user_id
) REFERENCES users
(
  id
)
  );

-- 6. Hospital Address Table
CREATE TABLE IF NOT EXISTS hospital_address
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  address_line1
  VARCHAR
(
  255
) NOT NULL,
  address_line2 VARCHAR
(
  255
),
  city VARCHAR
(
  100
) NOT NULL,
  country VARCHAR
(
  100
) NOT NULL,
  landmark VARCHAR
(
  255
),
  phone_number VARCHAR
(
  50
),
  postal_code VARCHAR
(
  20
) NOT NULL,
  state VARCHAR
(
  100
) NOT NULL,
  hospital_id BIGINT NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT hospital_address_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_hosp_addr_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
)
  );

-- 7. Hospital Departments Table
CREATE TABLE IF NOT EXISTS hospital_departments
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  active
  BOOLEAN
  NOT
  NULL
  DEFAULT
  TRUE,
  department_id
  BIGINT
  NOT
  NULL,
  head_doctor_id
  BIGINT
  UNIQUE,
  hospital_id
  BIGINT
  NOT
  NULL,
  is_deleted
  BOOLEAN
  NOT
  NULL
  DEFAULT
  FALSE,
  CONSTRAINT
  hospital_departments_pkey
  PRIMARY
  KEY
(
  id
),
  CONSTRAINT fk_department_head_doctor FOREIGN KEY
(
  head_doctor_id
) REFERENCES doctors
(
  id
),
  CONSTRAINT fk_hd_department FOREIGN KEY
(
  department_id
) REFERENCES departments
(
  id
),
  CONSTRAINT fk_hd_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
)
  );

-- 8. Staffs Table
CREATE TABLE IF NOT EXISTS staffs
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  blood_group
  VARCHAR
(
  20
),
  date_of_birth DATE NOT NULL,
  emergency_contact VARCHAR
(
  50
),
  first_name VARCHAR
(
  100
) NOT NULL,
  gender VARCHAR
(
  20
),
  last_name VARCHAR
(
  100
) NOT NULL,
  phone VARCHAR
(
  50
),
  hospital_id BIGINT,
  user_id BIGINT NOT NULL UNIQUE,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT staffs_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_staff_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
),
  CONSTRAINT fk_staff_user FOREIGN KEY
(
  user_id
) REFERENCES users
(
  id
)
  );

-- 9. Appointments Table
CREATE TABLE IF NOT EXISTS appointments
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  appointment_date
  DATE
  NOT
  NULL,
  end_time
  TIME
  WITHOUT
  TIME
  ZONE
  NOT
  NULL,
  hospital_id
  BIGINT
  NOT
  NULL,
  start_time
  TIME
  WITHOUT
  TIME
  ZONE
  NOT
  NULL,
  status
  VARCHAR
(
  50
) NOT NULL,
  department_id BIGINT,
  doctor_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT appointments_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_appointment_department FOREIGN KEY
(
  department_id
) REFERENCES departments
(
  id
),
  CONSTRAINT fk_appointment_doctor FOREIGN KEY
(
  doctor_id
) REFERENCES doctors
(
  id
),
  CONSTRAINT fk_appointment_hospital FOREIGN KEY
(
  hospital_id
) REFERENCES hospitals
(
  id
),
  CONSTRAINT fk_appointment_patient FOREIGN KEY
(
  patient_id
) REFERENCES patients
(
  id
)
  );

-- 10. Insurances Table
CREATE TABLE IF NOT EXISTS insurances
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  coverage_amount
  DOUBLE
  PRECISION
  NOT
  NULL,
  deductible
  DOUBLE
  PRECISION,
  expiry_date
  DATE
  NOT
  NULL,
  policy_number
  VARCHAR
(
  100
) NOT NULL UNIQUE,
  policy_type VARCHAR
(
  100
) NOT NULL,
  provider_contact_email VARCHAR
(
  255
),
  provider_name VARCHAR
(
  255
) NOT NULL,
  provider_phone_number VARCHAR
(
  50
),
  start_date DATE NOT NULL,
  status VARCHAR
(
  50
) NOT NULL,
  patient_id BIGINT,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT insurances_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_insurance_patient FOREIGN KEY
(
  patient_id
) REFERENCES patients
(
  id
)
  );

-- 11. Medical Records Table
CREATE TABLE IF NOT EXISTS medical_records
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  diagnosis
  VARCHAR
(
  500
) NOT NULL,
  follow_up_notes VARCHAR
(
  1000
),
  hospital_id BIGINT NOT NULL,
  record_date DATE NOT NULL,
  status VARCHAR
(
  50
) NOT NULL,
  symptoms VARCHAR
(
  1000
),
  treatment_plan VARCHAR
(
  1000
),
  appointment_id BIGINT,
  doctor_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT medical_records_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_medical_record_appointment FOREIGN KEY
(
  appointment_id
) REFERENCES appointments
(
  id
),
  CONSTRAINT fk_medical_record_doctor FOREIGN KEY
(
  doctor_id
) REFERENCES doctors
(
  id
),
  CONSTRAINT fk_medical_record_patient FOREIGN KEY
(
  patient_id
) REFERENCES patients
(
  id
)
  );

-- 12. Prescription Table
CREATE TABLE IF NOT EXISTS prescription
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  dosage_instructions
  TEXT,
  medications
  TEXT
  NOT
  NULL,
  appointment_id
  BIGINT,
  doctor_id
  BIGINT
  NOT
  NULL,
  patient_id
  BIGINT
  NOT
  NULL,
  is_deleted
  BOOLEAN
  NOT
  NULL
  DEFAULT
  FALSE,
  CONSTRAINT
  prescription_pkey
  PRIMARY
  KEY
(
  id
),
  CONSTRAINT fk_prescription_appointment FOREIGN KEY
(
  appointment_id
) REFERENCES appointments
(
  id
),
  CONSTRAINT fk_prescription_doctor FOREIGN KEY
(
  doctor_id
) REFERENCES doctors
(
  id
),
  CONSTRAINT fk_prescription_patient FOREIGN KEY
(
  patient_id
) REFERENCES patients
(
  id
)
  );

-- 13. Address Table
CREATE TABLE IF NOT EXISTS address
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  address_line1
  VARCHAR
(
  255
) NOT NULL,
  address_line2 VARCHAR
(
  255
),
  address_type VARCHAR
(
  50
),
  city VARCHAR
(
  100
) NOT NULL,
  country VARCHAR
(
  100
) NOT NULL,
  is_default BOOLEAN NOT NULL,
  landmark VARCHAR
(
  255
),
  phone_number VARCHAR
(
  50
),
  postal_code VARCHAR
(
  20
) NOT NULL,
  state VARCHAR
(
  100
) NOT NULL,
  user_id BIGINT,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT address_pkey PRIMARY KEY
(
  id
),
  CONSTRAINT fk_address_user FOREIGN KEY
(
  user_id
) REFERENCES users
(
  id
)
  );

-- 14. OTP Tables
CREATE TABLE IF NOT EXISTS otp_tables
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  email
  VARCHAR
(
  255
) NOT NULL,
  expired_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         otp VARCHAR (10) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT otp_tables_pkey PRIMARY KEY
(
  id
)
  );

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_appointments_date ON appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX IF NOT EXISTS idx_otp_email ON otp_tables(email);


-- 1. Medication Table
CREATE TABLE IF NOT EXISTS medication
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  name
  VARCHAR
(
  255
) NOT NULL,
  manufacturer VARCHAR
(
  255
),
  dosage_form VARCHAR
(
  50
),
  unit_price DOUBLE PRECISION NOT NULL,
  stock_quantity INTEGER NOT NULL,
  reorder_level INTEGER NOT NULL,
  expiry_date DATE,
  batch_number VARCHAR
(
  100
),
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT medication_pkey PRIMARY KEY
(
  id
)
  );

-- 2. Dispense Records Table
CREATE TABLE IF NOT EXISTS dispense_records
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  patient_id
  BIGINT
  NOT
  NULL,
  prescription_id
  BIGINT,
  dispensed_by_id
  BIGINT
  NOT
  NULL,
  total_amount
  DOUBLE
  PRECISION
  NOT
  NULL,
  dispense_status
  VARCHAR
(
  20
) NOT NULL,
  payment_status VARCHAR
(
  20
) NOT NULL,
  dispense_date TIMESTAMP WITH TIME ZONE NOT NULL,
                            is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT dispense_records_pkey PRIMARY KEY (id),
  CONSTRAINT fk_dispense_patient FOREIGN KEY
(
  patient_id
) REFERENCES patients
(
  id
),
  CONSTRAINT fk_dispense_prescription FOREIGN KEY
(
  prescription_id
) REFERENCES prescription
(
  id
),
  CONSTRAINT fk_dispense_staff FOREIGN KEY
(
  dispensed_by_id
) REFERENCES staffs
(
  id
)
  );

-- Indexes for Dispense Records
CREATE INDEX IF NOT EXISTS idx_dispense_patient_id ON dispense_records(patient_id);
CREATE INDEX IF NOT EXISTS idx_dispense_prescription_id ON dispense_records(prescription_id);

-- 3. Dispense Items Table
CREATE TABLE IF NOT EXISTS dispense_items
(
  id
  BIGINT
  GENERATED
  ALWAYS AS
  IDENTITY
  NOT
  NULL,
  created_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  updated_at
  TIMESTAMP
  WITH
  TIME
  ZONE
  NOT
  NULL,
  dispense_record_id
  BIGINT
  NOT
  NULL,
  medication_id
  BIGINT
  NOT
  NULL,
  quantity
  INTEGER
  NOT
  NULL,
  unit_price
  DOUBLE
  PRECISION
  NOT
  NULL,
  sub_total
  DOUBLE
  PRECISION
  NOT
  NULL,
  is_deleted
  BOOLEAN
  NOT
  NULL
  DEFAULT
  FALSE,
  CONSTRAINT
  dispense_items_pkey
  PRIMARY
  KEY
(
  id
),
  CONSTRAINT fk_di_dispense_record FOREIGN KEY
(
  dispense_record_id
) REFERENCES dispense_records
(
  id
),
  CONSTRAINT fk_di_medication FOREIGN KEY
(
  medication_id
) REFERENCES medication
(
  id
)
  );

-- Indexes for Dispense Items
CREATE INDEX IF NOT EXISTS idx_di_dispense_record_id ON dispense_items(dispense_record_id);
CREATE INDEX IF NOT EXISTS idx_di_medication_id ON dispense_items(medication_id);

