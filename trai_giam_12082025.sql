
drop table criminal_id_record;
drop table detainee;
drop table fingerprint_carding;
drop table departments;
alter table identity_record rename column detainees to detainee_id;
drop table wards;
drop table provinces;

DROP TABLE detainees;

-- ================================
-- PHẦN 1: CÁC BẢNG DANH MỤC CHUNG
-- ================================
-- Bảng trại tạm giam
CREATE TABLE detention_centers (
       id SERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       code VARCHAR(50) NOT NULL UNIQUE,
       address TEXT,
       phone VARCHAR(20),
       email VARCHAR(100),
       director VARCHAR(100),
       deputy_director VARCHAR(100),
       established_date DATE,
       capacity INTEGER, -- Sức chứa tối đa
       current_population INTEGER DEFAULT 0, -- Số người hiện tại
       is_active BOOLEAN DEFAULT TRUE,
       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bảng đơn vị/phòng ban trong trại
CREATE TABLE departments (
     id SERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     code VARCHAR(50) NOT NULL,
     detention_center_id INTEGER not null ,
     description TEXT,
     is_active BOOLEAN DEFAULT TRUE,
     UNIQUE(code, detention_center_id)
);

-- Bảng dân tộc
CREATE TABLE ethnicities (
     id SERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL UNIQUE,
     code VARCHAR(10) NOT NULL UNIQUE
);

-- Bảng tôn giáo
CREATE TABLE religions (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL UNIQUE,
       code VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE COUNTRIES (
    num_code INTEGER NOT NULL primary key,
    alpha_2_code VARCHAR(2) default NULL,
    alpha_3_code VARCHAR(3) default NULL,
    en_short_name VARCHAR(52) default NULL,
    nationality VARCHAR(39) default NULL
);

-- Bảng trình độ học vấn
CREATE TABLE education_levels (
      id SERIAL PRIMARY KEY,
      name VARCHAR(100) NOT NULL UNIQUE,
      code VARCHAR(20) NOT NULL UNIQUE,
      level_order INTEGER
);


-- Bảng khu vực địa lý
CREATE TABLE administrative_regions (
    id integer NOT NULL,
    "name" varchar(255) NOT NULL,
    name_en varchar(255) NOT NULL,
    code_name varchar(255) NULL,
    code_name_en varchar(255) NULL,
    CONSTRAINT administrative_regions_pkey PRIMARY KEY (id)
);


-- Bảng danh sách các đơn vị hành chính
CREATE TABLE administrative_units (
      id integer NOT NULL,
      full_name varchar(255) NULL,
      full_name_en varchar(255) NULL,
      short_name varchar(255) NULL,
      short_name_en varchar(255) NULL,
      code_name varchar(255) NULL,
      code_name_en varchar(255) NULL,
      CONSTRAINT administrative_units_pkey PRIMARY KEY (id)
);


-- Bảng danh sách các tỉnh
CREATE TABLE provinces (
       code varchar(20) NOT NULL,
       "name" varchar(255) NOT NULL,
       name_en varchar(255) NULL,
       full_name varchar(255) NOT NULL,
       full_name_en varchar(255) NULL,
       code_name varchar(255) NULL,
       administrative_unit_id integer NULL,
       CONSTRAINT provinces_pkey PRIMARY KEY (code)
);


-- provinces foreign keys
ALTER TABLE provinces ADD CONSTRAINT provinces_administrative_unit_id_fkey FOREIGN KEY (administrative_unit_id) REFERENCES administrative_units(id);
CREATE INDEX idx_provinces_unit ON provinces(administrative_unit_id);


-- Bảng danh sách các xã
CREATE TABLE wards (
       code varchar(20) NOT NULL,
       "name" varchar(255) NOT NULL,
       name_en varchar(255) NULL,
       full_name varchar(255) NULL,
       full_name_en varchar(255) NULL,
       code_name varchar(255) NULL,
       province_code varchar(20) NULL,
       administrative_unit_id integer NULL,
       CONSTRAINT wards_pkey PRIMARY KEY (code)
);


-- wards foreign keys

ALTER TABLE wards ADD CONSTRAINT wards_administrative_unit_id_fkey FOREIGN KEY (administrative_unit_id) REFERENCES administrative_units(id);
ALTER TABLE wards ADD CONSTRAINT wards_province_code_fkey FOREIGN KEY (province_code) REFERENCES provinces(code);

CREATE INDEX idx_wards_province ON wards(province_code);
CREATE INDEX idx_wards_unit ON wards(administrative_unit_id);

-- Bảng chức vụ
CREATE TABLE positions (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL UNIQUE,
       code VARCHAR(20) NOT NULL UNIQUE,
       level INTEGER,
       description TEXT,
       applies_to VARCHAR(20) DEFAULT 'ALL' -- 'STAFF', 'DETAINEE', 'ALL'
);

-- ================================
-- PHẦN 2: QUẢN LÝ CÁN BỘ/NHÂN VIÊN
-- ================================

-- Bảng cán bộ/nhân viên
CREATE TABLE staff (
   id SERIAL PRIMARY KEY,

    -- Mã định danh
   staff_code VARCHAR(20) NOT NULL UNIQUE,
   profile_number VARCHAR(50), -- Số hồ sơ cán bộ

    -- Thông tin cơ bản
   full_name VARCHAR(255) NOT NULL,
   gender VARCHAR(10),
   date_of_birth DATE,
   place_of_birth TEXT,
   id_number VARCHAR(20) UNIQUE,
   id_issue_date DATE,
   id_issue_place VARCHAR(255),

    -- Thông tin dân tộc, tôn giáo
   ethnicity_id INTEGER,
   religion_id INTEGER,

    -- Địa chỉ
   permanent_address TEXT,
   permanent_ward_id varchar(20),
   permanent_province_id varchar(20),

   temporary_address TEXT,
   temporary_ward_id varchar(20),
   temporary_province_id varchar(20),

    -- Thông tin liên hệ
   phone VARCHAR(20),
   email VARCHAR(100),
   emergency_contact VARCHAR(255),
   emergency_phone VARCHAR(20),

    -- Thông tin công việc
   detention_center_id INTEGER,
   department_id INTEGER,
   position_id INTEGER,
   rank VARCHAR(50), -- Cấp bậc quân đội/công an

    -- Thông tin học vấn
   education_level_id INTEGER,

    -- Trạng thái
   status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, RETIRED, TRANSFERRED
   is_active BOOLEAN DEFAULT TRUE,

    -- Timestamps
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- PHẦN 2: QUẢN LÝ PHAM NHÂN
-- ================================
-- Bảng phạm nhân
CREATE TABLE detainees (
   id Bigserial PRIMARY KEY,

    -- Mã định danh
   detainee_code VARCHAR(20) NOT NULL UNIQUE,
   profile_number VARCHAR(50), -- Số hồ sơ phạm nhân

    -- Thông tin cơ bản
   full_name VARCHAR(255) NOT NULL,
   alias_name VARCHAR(255), -- Tên gọi khác
   gender VARCHAR(10),
   date_of_birth DATE,
   place_of_birth TEXT,
   id_number VARCHAR(20),
   id_issue_date DATE,
   id_issue_place VARCHAR(255),

    -- Thông tin dân tộc, tôn giáo, quốc tịch
    nationality_id INTEGER,
   ethnicity_id INTEGER,
   religion_id INTEGER,

    -- Địa chỉ
   permanent_address TEXT,
   permanent_ward_id VARCHAR(20),
   permanent_province_id varchar(20),

   temporary_address TEXT,
   temporary_ward_id varchar(20),
   temporary_province_id varchar(20),

    current_address TEXT, -- Địa chỉ hiện tại (nếu khác)
    current_ward_id VARCHAR(20),
    current_province_id varchar(20),

    -- nghề nghiệp
   occupation Text,

    -- Thông tin gia đình
   father_name VARCHAR(255),
   mother_name VARCHAR(255),
   spouse_name VARCHAR(255),

    -- Thông tin pháp lý
   detention_date DATE NOT NULL,
   expected_release_date DATE,
   actual_release_date DATE,
   case_number VARCHAR(50),
   charges TEXT, -- Tội danh
   sentence_duration VARCHAR(50), -- Thời hạn án
   court_name VARCHAR(255), -- Tòa án xét xử

    -- Thông tin giam giữ
   detention_center_id INTEGER,
   cell_number VARCHAR(20), -- Số buồng giam

    -- Trạng thái
   status VARCHAR(20) DEFAULT 'DETAINED', -- DETAINED, RELEASED, TRANSFERRED, DECEASED

    -- Ghi chú

   notes TEXT,

    -- Timestamps
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--Bảng danh bản
CREATE TABLE identity_record (
     id               BIGSERIAL PRIMARY KEY,
     detainees       BIGINT NOT NULL,
     created_place    TEXT,                       -- Tại
     reason_note      TEXT,                       -- Lập về việc
     arrest_date      DATE,                       -- Bắt ngày
     arrest_unit      TEXT,                       -- Đơn vị bắt
     fp_classification TEXT,                      -- C/T vân tay
     dp               TEXT,                       -- ĐP (nếu có)
     tw               TEXT,                       -- TW (nếu có)
     ak_file_no       TEXT,                       -- Hồ sơ AK số
     notes            TEXT,
     created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Đặc điểm hình thái học trong Danh bản
CREATE TABLE anthropometry (
   id                 BIGSERIAL PRIMARY KEY,
   identity_record_id BIGINT NOT NULL,
   face_shape         TEXT,                     -- Khuôn mặt
   height_cm          NUMERIC(5,2),
   nose_bridge        TEXT,                     -- Sống mũi
   distinctive_marks  TEXT,                     -- Dấu vết riêng
   ear_lower_fold     TEXT,                     -- Nếp tai dưới
   ear_lobe           TEXT,                     -- Dái tai
   created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bảng ảnh trong Danh bản 'FRONT','LEFT_PROFILE','RIGHT_PROFILE'
CREATE TABLE photo (
   id                 BIGSERIAL PRIMARY KEY,
   identity_record_id BIGINT NOT NULL ,
   view               varchar(20) NOT NULL,    -- 'FRONT', 'LEFT_PROFILE', 'RIGHT_PROFILE'
   bucket             TEXT NOT NULL,            -- tên bucket
   object_key         TEXT NOT NULL,            -- key trong bucket
    object_url         TEXT NOT NULL,            -- URL truy cập ảnh
   mime_type          TEXT,
   size_bytes         BIGINT,
   created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   UNIQUE(identity_record_id, view)
);

-- Bảng chỉ bản
CREATE TABLE fingerprint_card (
  id                BIGSERIAL PRIMARY KEY,
  person_id         BIGINT NOT NULL,
  created_date      DATE NOT NULL,             -- Lập ngày
  created_place     TEXT,                      -- Tại
  dp                TEXT,
  tw                TEXT,
  fp_formula        TEXT,                      -- Công thức vân tay
  reason_note       TEXT,                      -- Lập về việc
  created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Ảnh/WSQ/template vân tay của từng ô
CREATE TABLE fingerprint_impression (
    id                  BIGSERIAL PRIMARY KEY,
    fingerprint_card_id BIGINT NOT NULL,
    finger              varchar(20),    --'RIGHT_THUMB','RIGHT_INDEX','RIGHT_MIDDLE','RIGHT_RING','RIGHT_LITTLE','LEFT_THUMB','LEFT_INDEX','LEFT_MIDDLE','LEFT_RING','LEFT_LITTLE'  -- bắt buộc khi kind = ROLLED hoặc PLAIN_SINGLE
    kind                varchar(20),  --'ROLLED'-lăn từng ngón (10 ô 1..10), 'PLAIN_SINGLE'-ấn phẳng 1 ngón (nếu tách riêng),'PLAIN_RIGHT_FOUR','PLAIN_LEFT_FOUR','PLAIN_LEFT_THUMBS', 'PLAIN_RIGHT_THUMBS'
    bucket              TEXT NOT NULL,
    image_key           TEXT NOT NULL,           -- ảnh/WSQ của ô tương ứng
    object_url          TEXT NOT NULL,           -- URL truy cập ảnh/WSQ
    quality_score       SMALLINT,                -- 0..100 (tuỳ SDK)
    captured_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(fingerprint_card_id, kind, finger)
);


-- Bảng lịch sử giam giữ
CREATE TABLE detention_history (
   id SERIAL PRIMARY KEY,
   detainee_id INTEGER REFERENCES detainees(id),
   detention_center_id INTEGER REFERENCES detention_centers(id),
   cell_number VARCHAR(20),
   start_date DATE NOT NULL,
   end_date DATE,
   reason TEXT, -- Lý do giam giữ
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   UNIQUE(detainee_id, start_date) -- Mỗi phạm nhân chỉ có một lịch sử giam giữ tại một thời điểm
);

-- Bảng lịch sử chuyển trại
CREATE TABLE transfer_history (
    id SERIAL PRIMARY KEY,
    detainee_id INTEGER REFERENCES detainees(id),
    from_detention_center_id INTEGER REFERENCES detention_centers(id),
    to_detention_center_id INTEGER REFERENCES detention_centers(id),
    transfer_date DATE NOT NULL,
    reason TEXT, -- Lý do chuyển trại
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(detainee_id, transfer_date) -- Mỗi phạm nhân chỉ có một lịch sử chuyển trại tại một thời điểm
);

CREATE INDEX IF NOT EXISTS idx_wards_province    ON wards (province_code);
CREATE INDEX IF NOT EXISTS idx_staff_perm_ward   ON staff (permanent_ward_id);
CREATE INDEX IF NOT EXISTS idx_staff_temp_ward   ON staff (temporary_ward_id);

CREATE INDEX IF NOT EXISTS idx_staff_ethnicity_id       ON staff (ethnicity_id);
CREATE INDEX IF NOT EXISTS idx_staff_religion_id        ON staff (religion_id);
CREATE INDEX IF NOT EXISTS idx_staff_department_id      ON staff (department_id);
CREATE INDEX IF NOT EXISTS idx_staff_position_id        ON staff (position_id);
CREATE INDEX IF NOT EXISTS idx_staff_education_level_id ON staff (education_level_id);
CREATE INDEX IF NOT EXISTS idx_staff_detention_center_id ON staff (detention_center_id);
