[
  {
    "json_agg": [
      {
        "table_name": "clients",
        "column_name": "id",
        "data_type": "uuid",
        "is_nullable": "NO",
        "column_default": "uuid_generate_v4()"
      },
      {
        "table_name": "clients",
        "column_name": "name",
        "data_type": "character varying",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "clients",
        "column_name": "document_number",
        "data_type": "character varying",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "clients",
        "column_name": "active",
        "data_type": "boolean",
        "is_nullable": "YES",
        "column_default": "true"
      },
      {
        "table_name": "clients",
        "column_name": "created_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "clients",
        "column_name": "updated_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "filling_rules",
        "column_name": "id",
        "data_type": "uuid",
        "is_nullable": "NO",
        "column_default": "uuid_generate_v4()"
      },
      {
        "table_name": "filling_rules",
        "column_name": "client_id",
        "data_type": "uuid",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "filling_rules",
        "column_name": "description",
        "data_type": "text",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "filling_rules",
        "column_name": "rule_definition",
        "data_type": "jsonb",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "filling_rules",
        "column_name": "is_active",
        "data_type": "boolean",
        "is_nullable": "YES",
        "column_default": "true"
      },
      {
        "table_name": "filling_rules",
        "column_name": "created_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "spreadsheets",
        "column_name": "id",
        "data_type": "uuid",
        "is_nullable": "NO",
        "column_default": "uuid_generate_v4()"
      },
      {
        "table_name": "spreadsheets",
        "column_name": "client_id",
        "data_type": "uuid",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "spreadsheets",
        "column_name": "original_filename",
        "data_type": "character varying",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "spreadsheets",
        "column_name": "storage_path",
        "data_type": "text",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "spreadsheets",
        "column_name": "status",
        "data_type": "USER-DEFINED",
        "is_nullable": "YES",
        "column_default": "'RECEBIDA'::status_processamento"
      },
      {
        "table_name": "spreadsheets",
        "column_name": "processing_logs",
        "data_type": "text",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "spreadsheets",
        "column_name": "ai_metadata",
        "data_type": "jsonb",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "spreadsheets",
        "column_name": "created_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "spreadsheets",
        "column_name": "updated_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "spreadsheets",
        "column_name": "processed_by",
        "data_type": "uuid",
        "is_nullable": "YES",
        "column_default": null
      },
      {
        "table_name": "users",
        "column_name": "id",
        "data_type": "uuid",
        "is_nullable": "NO",
        "column_default": "uuid_generate_v4()"
      },
      {
        "table_name": "users",
        "column_name": "username",
        "data_type": "character varying",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "users",
        "column_name": "email",
        "data_type": "character varying",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "users",
        "column_name": "password",
        "data_type": "character varying",
        "is_nullable": "NO",
        "column_default": null
      },
      {
        "table_name": "users",
        "column_name": "role",
        "data_type": "USER-DEFINED",
        "is_nullable": "YES",
        "column_default": "'OPERATOR'::user_role"
      },
      {
        "table_name": "users",
        "column_name": "active",
        "data_type": "boolean",
        "is_nullable": "YES",
        "column_default": "true"
      },
      {
        "table_name": "users",
        "column_name": "created_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      },
      {
        "table_name": "users",
        "column_name": "updated_at",
        "data_type": "timestamp with time zone",
        "is_nullable": "YES",
        "column_default": "now()"
      }
    ]
  }
]