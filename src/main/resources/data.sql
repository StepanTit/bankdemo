-- Legacy rows created before @Version.
UPDATE accounts SET version = 0 WHERE version IS NULL;
