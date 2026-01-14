-- Set 19 MGI markers to internal='true'

UPDATE biologicalentity
SET internal = true
WHERE primaryexternalid IN (
    'MGI:1891407',
    'MGI:2183341',
    'MGI:3766098',
    'MGI:3772360',
    'MGI:3785283',
    'MGI:3790484',
    'MGI:5315448',
    'MGI:5441844',
    'MGI:7012350',
    'MGI:7384456',
    'MGI:7447479',
    'MGI:7868081',
    'MGI:95903',
    'MGI:96014',
    'MGI:96020',
    'MGI:96461',
    'MGI:98376',
    'MGI:98553',
    'MGI:98578'
);
