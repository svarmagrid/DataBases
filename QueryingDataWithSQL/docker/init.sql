DROP TABLE IF EXISTS person_country;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS continents;
DROP TABLE IF EXISTS countries;

CREATE TABLE continents(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE countries(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    continent_id INT NOT NULL REFERENCES continents(id) ON DELETE CASCADE,
    population BIGINT NOT NULL CHECK (population>=0),
    area NUMERIC(15,2) NOT NULL CHECK (area>0)
);


CREATE TABLE people(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);


CREATE TABLE person_country(
    person_id INT REFERENCES people(id) ON DELETE CASCADE,
    country_id INT REFERENCES countries(id) ON DELETE CASCADE,
    PRIMARY KEY(person_id, country_id)
);


-- Continents
INSERT INTO continents (name) VALUES
('Asia'), ('Europe'), ('Africa'),
('North America'), ('South America'), ('Australia'), ('Antartica');

-- Countries
INSERT INTO countries (name, continent_id, population, area) VALUES
   ('India', 1, 1400000000, 3287263),
   ('China', 1, 1410000000, 9596961),
   ('France', 2, 67000000, 551695),
   ('Germany', 2, 83000000, 357022),
   ('Nigeria', 3, 223000000, 923768),
   ('Egypt', 3, 110000000, 1002450),
   ('USA', 4, 331000000, 9833517),
   ('Canada', 4, 38000000, 9984670),
   ('Brazil', 5, 215000000, 8515767),
   ('Argentina', 5, 46000000, 2780400),
   ('Australia', 6, 26000000, 7692024);

-- People
INSERT INTO people (name) VALUES
   ('John'), ('Alice'), ('Bob'), ('Charlie'), ('David'),
   ('Eve'), ('Frank'), ('Grace'), ('Heidi'), ('Ivan');

-- Citizenship
INSERT INTO person_country (person_id, country_id) VALUES
    (1,1),
    (1,7),
    (2,3),
    (3,4),
    (3,2),
    (4,5),
    (5,6),
    (6,7),
    (6,8),
    (6,2),
    (7,9),
    (8,10),
    (9,11);
-- person 10 has no citizenship

select ID, NAME from countries order by population desc limit 1;

select name, population from countries order by (population::numeric/area) asc limit 10;

select name from countries where (population::numeric/area) > (select AVG(population::numeric/area) from countries );

select name from countries where length(name) = (select max(length(name)) from countries );

select name from countries where name ilike '%f%' order by name;

SELECT name,population
FROM countries
ORDER BY ABS(population - (SELECT AVG(population) FROM countries))
    LIMIT 1;

SELECT c.name, COUNT(ct.id) AS country_count
FROM continents c
         LEFT JOIN countries ct ON c.id = ct.continent_id
GROUP BY c.name;

select c.name, sum(ct.area) as total_area
from continents c
         join countries ct on c.id=ct.continent_id
group by c.name
order by total_area desc;

select c.name, avg(ct.population::numeric / ct.area) as avg_density
from continents c
         join countries ct on c.id =ct.continent_id
group by c.name
order by avg_density;

select distinct on (c.id) c.name as continent, ct.name as country, ct.area
from continents c
    join countries ct on c.id = ct.continent_id
order by c.id, ct.area desc;

select c.name
from continents c
         join countries c2 on c.id=c2.continent_id
group by c.name
having avg(c2.population) < 200000000;

select p.id, p.name, count(ps.country_id) as citizenship
from people p
         join person_country ps on p.id=ps.person_id
group by p.id
order by citizenship desc
    limit 1;

select p.id, p.name
from people p
         left join person_country pc on p.id=pc.person_id
where pc.person_id is null;

select c.name as country
from countries c
         join person_country pc on c.id=pc.country_id
group by c.id
order by count(pc.person_id )
    limit 1;

select con.name as continent
from continents con
         join countries c2 on con.id=c2.continent_id
         join person_country pc on c2.id=pc.country_id
group by con.name
order by count(pc.person_id) desc
    limit 1;

insert into people (name) values ('Grace');

SELECT p1.id, p2.id, p1.name
FROM people p1
         JOIN people p2
              ON p1.name = p2.name AND p1.id < p2.id;




