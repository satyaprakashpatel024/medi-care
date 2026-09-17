-- V2__add_pharmacy_schema.sql
-- Adds tables for Pharmacy Management (Medication, DispenseRecord, DispenseItem)

-- 1. Medication Table
CREATE TABLE IF NOT EXISTS medication (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255),
    dosage_form VARCHAR(50),
    unit_price DOUBLE PRECISION NOT NULL,
    stock_quantity INTEGER NOT NULL,
    reorder_level INTEGER NOT NULL,
    expiry_date DATE,
    batch_number VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT medication_pkey PRIMARY KEY (id)
);

-- 2. Dispense Records Table
CREATE TABLE IF NOT EXISTS dispense_records (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    patient_id BIGINT NOT NULL,
    prescription_id BIGINT,
    dispensed_by_id BIGINT NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    dispense_status VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    dispense_date TIMESTAMP WITH TIME ZONE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT dispense_records_pkey PRIMARY KEY (id),
    CONSTRAINT fk_dispense_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_dispense_prescription FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    CONSTRAINT fk_dispense_staff FOREIGN KEY (dispensed_by_id) REFERENCES staffs(id)
);

-- Indexes for Dispense Records
CREATE INDEX IF NOT EXISTS idx_dispense_patient_id ON dispense_records(patient_id);
CREATE INDEX IF NOT EXISTS idx_dispense_prescription_id ON dispense_records(prescription_id);

-- 3. Dispense Items Table
CREATE TABLE IF NOT EXISTS dispense_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    dispense_record_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    sub_total DOUBLE PRECISION NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT dispense_items_pkey PRIMARY KEY (id),
    CONSTRAINT fk_di_dispense_record FOREIGN KEY (dispense_record_id) REFERENCES dispense_records(id),
    CONSTRAINT fk_di_medication FOREIGN KEY (medication_id) REFERENCES medication(id)
);

-- Indexes for Dispense Items
CREATE INDEX IF NOT EXISTS idx_di_dispense_record_id ON dispense_items(dispense_record_id);
CREATE INDEX IF NOT EXISTS idx_di_medication_id ON dispense_items(medication_id);
