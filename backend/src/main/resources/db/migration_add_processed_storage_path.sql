-- Adiciona coluna para armazenar o caminho do arquivo corrigido (planilha com NCM/CEST corrigidos).
-- Execute uma vez no banco (ex.: Supabase SQL Editor) se usar profile prod com ddl-auto: validate.

ALTER TABLE spreadsheets
ADD COLUMN IF NOT EXISTS processed_storage_path VARCHAR(512);

COMMENT ON COLUMN spreadsheets.processed_storage_path IS 'Caminho do arquivo Excel corrigido no storage (bucket ou disco)';
