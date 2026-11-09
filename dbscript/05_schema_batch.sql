-- ============================================================
-- 05. Spring Batch 메타 테이블 생성
-- Spring Batch 5.x 표준 스키마 (MariaDB / MySQL 호환)
-- 출처: spring-batch-core org.springframework.batch.core.repository.db
-- ============================================================

-- ---------- BATCH_JOB_INSTANCE ----------
CREATE TABLE BATCH_JOB_INSTANCE (
    JOB_INSTANCE_ID       BIGINT       NOT NULL,
    VERSION               BIGINT       NULL,
    JOB_NAME              VARCHAR(100) NOT NULL,
    JOB_KEY               VARCHAR(32)  NOT NULL,
    PRIMARY KEY (JOB_INSTANCE_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_JOB_EXECUTION ----------
CREATE TABLE BATCH_JOB_EXECUTION (
    JOB_EXECUTION_ID      BIGINT       NOT NULL,
    VERSION               BIGINT       NULL,
    JOB_INSTANCE_ID       BIGINT       NOT NULL,
    CREATE_TIME           DATETIME     NOT NULL,
    START_TIME            DATETIME     NULL,
    END_TIME              DATETIME     NULL,
    STATUS                VARCHAR(10)  NULL,
    EXIT_CODE             VARCHAR(2500) NULL,
    EXIT_MESSAGE          VARCHAR(2500) NULL,
    LAST_UPDATED          DATETIME     NULL,
    PRIMARY KEY (JOB_EXECUTION_ID),
    CONSTRAINT fk_job_exec_job_instance FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_JOB_EXECUTION_PARAMS ----------
CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
    JOB_EXECUTION_ID      BIGINT       NOT NULL,
    PARAMETER_NAME         VARCHAR(100) NOT NULL,
    PARAMETER_TYPE         VARCHAR(100) NOT NULL,
    PARAMETER_VALUE        VARCHAR(2500) NULL,
    IDENTIFYING_CHAR       CHAR(1)     NOT NULL,
    KEY idx_job_exec_params (JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_JOB_EXECUTION_CONTEXT ----------
CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
    JOB_EXECUTION_ID       BIGINT       NOT NULL,
    SHORT_CONTEXT          VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT     TEXT         NULL,
    PRIMARY KEY (JOB_EXECUTION_ID),
    CONSTRAINT fk_job_exec_context FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_STEP_EXECUTION ----------
CREATE TABLE BATCH_STEP_EXECUTION (
    STEP_EXECUTION_ID      BIGINT       NOT NULL,
    VERSION               BIGINT       NOT NULL,
    STEP_NAME             VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID       BIGINT       NOT NULL,
    CREATE_TIME           DATETIME     NOT NULL,
    START_TIME            DATETIME     NULL,
    END_TIME              DATETIME     NULL,
    STATUS                VARCHAR(10)  NULL,
    COMMIT_COUNT          BIGINT       NULL,
    READ_COUNT            BIGINT       NULL,
    FILTER_COUNT          BIGINT       NULL,
    WRITE_COUNT           BIGINT       NULL,
    READ_SKIP_COUNT       BIGINT       NULL,
    WRITE_SKIP_COUNT      BIGINT       NULL,
    PROCESS_SKIP_COUNT    BIGINT       NULL,
    ROLLBACK_COUNT        BIGINT       NULL,
    EXIT_CODE             VARCHAR(2500) NULL,
    EXIT_MESSAGE          VARCHAR(2500) NULL,
    LAST_UPDATED          DATETIME     NULL,
    PRIMARY KEY (STEP_EXECUTION_ID),
    KEY idx_step_exec_job (JOB_EXECUTION_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_STEP_EXECUTION_CONTEXT ----------
CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
    STEP_EXECUTION_ID      BIGINT       NOT NULL,
    SHORT_CONTEXT          VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT     TEXT         NULL,
    PRIMARY KEY (STEP_EXECUTION_ID),
    CONSTRAINT fk_step_exec_context FOREIGN KEY (STEP_EXECUTION_ID) REFERENCES BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
) ENGINE=InnoDB;

-- ---------- BATCH_JOB_SEQ (시퀀스) ----------
CREATE TABLE BATCH_JOB_SEQ (
    ID                    BIGINT       NOT NULL,
    UNIQUE_KEY            CHAR(1)      NOT NULL,
    PRIMARY KEY (UNIQUE_KEY)
) ENGINE=InnoDB;

-- ---------- BATCH_JOB_EXECUTION_SEQ (시퀀스) ----------
CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
    ID                    BIGINT       NOT NULL,
    UNIQUE_KEY            CHAR(1)      NOT NULL,
    PRIMARY KEY (UNIQUE_KEY)
) ENGINE=InnoDB;

-- ---------- BATCH_STEP_EXECUTION_SEQ (시퀀스) ----------
CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
    ID                    BIGINT       NOT NULL,
    UNIQUE_KEY            CHAR(1)      NOT NULL,
    PRIMARY KEY (UNIQUE_KEY)
) ENGINE=InnoDB;