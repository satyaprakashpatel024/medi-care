-- Migration SQL Script to update users_role_check constraint in PostgreSQL

-- 1. Drop existing constraint
ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_role_check;

-- 2. Migrate existing user roles if any exist
UPDATE public.users SET role = 'SUPER_ADMIN' WHERE role = 'ADMIN';
UPDATE public.users SET role = 'HOSPITAL_ADMIN' WHERE role = 'OWNER';

-- 3. Re-create constraint with updated roles (SUPER_ADMIN and HOSPITAL_ADMIN)
ALTER TABLE public.users ADD CONSTRAINT users_role_check 
CHECK (role::text = ANY (ARRAY[
    'PATIENT'::character varying, 
    'DOCTOR'::character varying, 
    'STAFF'::character varying, 
    'SUPER_ADMIN'::character varying, 
    'HOSPITAL_ADMIN'::character varying, 
    'RECEPTIONIST'::character varying, 
    'GUEST'::character varying
]::text[]));
