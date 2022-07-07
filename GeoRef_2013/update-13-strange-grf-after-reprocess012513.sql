select count(*) from grf_thesaurus_2013;  -- 32078

select count(*) from grf_thesaurus@eia;  -- 31782

delete from grf_thesaurus_2013;


select ((select count(t_id) from grf_thesaurus_2013)
-
(select count(t_id) from grf_thesaurus@eia)) from dual;   -- 296
---------------------------------

select narrower_terms from grf_thesaurus_2013 where main_term_search='texas';

select * from grf_thesaurus@eia where main_term_search='united states';

select * from grf_thesaurus@eia where main_term_search='texas';

------------------------------------



select * from grf_thesaurus_2013 where main_term_search='texas';



---------------------------- update 13 grf strabge records after reload

select count(*) from grf_thesaurus_2013 where main_term_search like '%&#8216%';  -- 13

select main_term_search from grf_thesaurus where main_term_search like '%&#8216%';

--
hco<3&#8216;>-&#8216;  -- 2
h<2&#8216;so<4&#8216;   -- 3
clo<4&#8216;>-&#8216;   -- 4
co<3&#8216;>2-&#8216;  -- 5
ch<3&#8216;cooh      --6
so<4&#8216;>2-&#8216;   --1
hno<3&#8216;       -- 7
po<4&#8216;>3-&#8216;   -- 8
nh<4&#8216;>+&#8216;   -- 9
no<2&#8216;>-&#8216;   --10
no<3&#8216;>-&#8216;   -- 11
oh>-&#8216;   -- 12
uo<2&#8216;>2+&#8216;  -- 13

-----------------------------------------
----- 1
select * from grf_thesaurus_2013 where main_term_search like 'so<4%';
select * from grf_thesaurus@eia where main_term_search like 'so4%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'so4%')
where main_term_search like 'so<4%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'so4%')
where main_term_search like 'so<4%';

select * from grf_thesaurus_2013 where main_term_search like 'so4%';

---------- updated use_terms

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'so4%')
where main_term_search like 'so4%';

select * from grf_thesaurus_2013 where main_term_search like 'so4%';

-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'so4%')
where main_term_search like 'so4%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'so4%';  -- sulfate ion
select * from grf_thesaurus_2013 where main_term_search='sulfate ion';
select * from grf_thesaurus@eia where main_term_search='sulfate ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='sulfate ion')
where main_term_search='sulfate ion';

--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='sulfate ion';
select * from grf_thesaurus@eia where main_term_search='sulfate ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='sulfate ion')
where main_term_search='sulfate ion';

------------------- 2

select * from grf_thesaurus_2013 where main_term_search like 'hco%';
select * from grf_thesaurus@eia where main_term_search like 'hco%';


update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'hco%')
where main_term_search like 'hco%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'hco%')
where main_term_search like 'hco%';

select * from grf_thesaurus_2013 where main_term_search like 'hco%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'hco%')
where main_term_search like 'hco%';

---------- updated use_terms

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'hco%')
where main_term_search like 'hco%';



---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'hco%';  -- sulfate ion
select * from grf_thesaurus_2013 where main_term_search='bicarbonate ion';
select * from grf_thesaurus@eia where main_term_search='bicarbonate ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='bicarbonate ion')
where main_term_search='bicarbonate ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='bicarbonate ion';
select * from grf_thesaurus@eia where main_term_search='bicarbonate ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='bicarbonate ion')
where main_term_search='bicarbonate ion';

----------------------3


select * from grf_thesaurus_2013 where main_term_search like 'h<2%';
select * from grf_thesaurus@eia where main_term_search like 'h2%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'h2%')
where main_term_search like 'h<2%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'h2%')
where main_term_search like 'h<2%';

select * from grf_thesaurus_2013 where main_term_search like 'h2%';

---------- updated use_terms

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'h2%')
where main_term_search like 'h2%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'h2%')
where main_term_search like 'h2%';

select * from grf_thesaurus_2013 where main_term_search like 'h2%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'h2%';  -- sulfate ion
select * from grf_thesaurus_2013 where main_term_search='sulfuric acid';
select * from grf_thesaurus@eia where main_term_search='sulfuric acid';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='sulfuric acid')
where main_term_search='sulfuric acid';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='sulfuric acid';
select * from grf_thesaurus@eia where main_term_search='sulfuric acid';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='sulfuric acid')
where main_term_search='sulfuric acid';

--------------------4

select * from grf_thesaurus_2013 where main_term_search like 'clo<4%';
select * from grf_thesaurus@eia where main_term_search like 'clo4%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'clo4%')
where main_term_search like 'clo<4%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'clo4%')
where main_term_search like 'clo<4%';

select * from grf_thesaurus_2013 where main_term_search like 'clo4%';

---------- updated use_terms

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'clo4%')
where main_term_search like 'clo4%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'clo4%')
where main_term_search like 'clo4%';

select * from grf_thesaurus_2013 where main_term_search like 'clo4%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'clo4%';  -- sulfate ion
select * from grf_thesaurus_2013 where main_term_search='perchlorate';
select * from grf_thesaurus@eia where main_term_search='perchlorate';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='perchlorate')
where main_term_search='perchlorate';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='perchlorate';
select * from grf_thesaurus@eia where main_term_search='perchlorate';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='perchlorate')
where main_term_search='perchlorate';


----------------------5

select * from grf_thesaurus_2013 where main_term_search like 'co<3%';
select * from grf_thesaurus@eia where main_term_search like 'co3%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'co3%')
where main_term_search like 'co<3%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'co3%')
where main_term_search like 'co<3%';

select * from grf_thesaurus_2013 where main_term_search like 'co3%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'co3%')
where main_term_search like 'co3%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'co3%')
where main_term_search like 'co3%';

select * from grf_thesaurus_2013 where main_term_search like 'co3%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'co3%';  -- sulfate ion
select * from grf_thesaurus_2013 where main_term_search='carbonate ion';
select * from grf_thesaurus@eia where main_term_search='carbonate ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='carbonate ion')
where main_term_search='carbonate ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='carbonate ion';
select * from grf_thesaurus@eia where main_term_search='carbonate ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='carbonate ion')
where main_term_search='carbonate ion';


---------------------- 6

select * from grf_thesaurus_2013 where main_term_search like 'ch<3%';
select * from grf_thesaurus@eia where main_term_search like 'ch3%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'ch3%')
where main_term_search like 'ch<3%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'ch3%')
where main_term_search like 'ch<3%';

select * from grf_thesaurus_2013 where main_term_search like 'ch3%';


update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'ch3%')
where main_term_search like 'ch3%';



-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'ch3%')
where main_term_search like 'ch3%';

select * from grf_thesaurus_2013 where main_term_search like 'ch3%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'ch3%';  
select * from grf_thesaurus_2013 where main_term_search='acetic acid';
select * from grf_thesaurus@eia where main_term_search='acetic acid';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='acetic acid')
where main_term_search='acetic acid';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='acetic acid';
select * from grf_thesaurus@eia where main_term_search='acetic acid';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='acetic acid')
where main_term_search='acetic acid';
------------------------------7

select * from grf_thesaurus_2013 where main_term_search like 'hno<3%';
select * from grf_thesaurus@eia where main_term_search like 'hno3%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'hno3%')
where main_term_search like 'hno<3%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'hno3%')
where main_term_search like 'hno<3%';

select * from grf_thesaurus_2013 where main_term_search like 'hno3%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'hno3%')
where main_term_search like 'hno3%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'hno3%')
where main_term_search like 'hno3%';

select * from grf_thesaurus_2013 where main_term_search like 'hno3%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'hno3%';  
select * from grf_thesaurus_2013 where main_term_search='nitric acid';
select * from grf_thesaurus@eia where main_term_search='nitric acid';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='nitric acid')
where main_term_search='nitric acid';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='nitric acid';
select * from grf_thesaurus@eia where main_term_search='nitric acid';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='nitric acid')
where main_term_search='nitric acid';

----------------------8

select * from grf_thesaurus_2013 where main_term_search like 'po<4%';
select * from grf_thesaurus@eia where main_term_search like 'po4%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'po4%')
where main_term_search like 'po<4%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'po4%')
where main_term_search like 'po<4%';

select * from grf_thesaurus_2013 where main_term_search like 'po4%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'po4%')
where main_term_search like 'po4%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'po4%';  
select * from grf_thesaurus_2013 where main_term_search='phosphate ion';
select * from grf_thesaurus@eia where main_term_search='phosphate ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='phosphate ion')
where main_term_search='phosphate ion';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'po4%')
where main_term_search like 'po4%';

select * from grf_thesaurus_2013 where main_term_search like 'po4%';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='phosphate ion';
select * from grf_thesaurus@eia where main_term_search='phosphate ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='phosphate ion')
where main_term_search='phosphate ion';


---------------------9

select * from grf_thesaurus_2013 where main_term_search like 'nh<4%';
select * from grf_thesaurus@eia where main_term_search like 'nh4%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'nh4%')
where main_term_search like 'nh<4%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'nh4%')
where main_term_search like 'nh<4%';

select * from grf_thesaurus_2013 where main_term_search like 'nh4%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'nh4%')
where main_term_search like 'nh4%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'nh4%')
where main_term_search like 'nh4%';

select * from grf_thesaurus_2013 where main_term_search like 'nh4%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'nh4%';  
select * from grf_thesaurus_2013 where main_term_search='ammonium ion';
select * from grf_thesaurus@eia where main_term_search='ammonium ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='ammonium ion')
where main_term_search='ammonium ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='ammonium ion';
select * from grf_thesaurus@eia where main_term_search='ammonium ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='ammonium ion')
where main_term_search='ammonium ion';


----------------- 10

select * from grf_thesaurus_2013 where main_term_search like 'no<2%';
select * from grf_thesaurus@eia where main_term_search like 'no2-%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'no2-%')
where main_term_search like 'no<2%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'no2-%')
where main_term_search like 'no<2%';

select * from grf_thesaurus_2013 where main_term_search like 'no2%';
select * from grf_thesaurus@eia where main_term_search like 'no2%';

select * from grf_thesaurus_2013 where main_term_search like 'no2-%';
select * from grf_thesaurus@eia where main_term_search like 'no2-%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'no2-%')
where main_term_search like 'no2-%';

---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'no2-%';  
select * from grf_thesaurus_2013 where main_term_search='nitrite ion';
select * from grf_thesaurus@eia where main_term_search='nitrite ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='nitrite ion')
where main_term_search='nitrite ion';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'no2-%')
where main_term_search like 'no2-%';

select * from grf_thesaurus_2013 where main_term_search like 'no2-%';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='nitrite ion';
select * from grf_thesaurus@eia where main_term_search='nitrite ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='nitrite ion')
where main_term_search='nitrite ion';


------------------- 11

select * from grf_thesaurus_2013 where main_term_search like 'no<3%';
select * from grf_thesaurus@eia where main_term_search like 'no3%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'no3%')
where main_term_search like 'no<3%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'no3%')
where main_term_search like 'no<3%';

select * from grf_thesaurus_2013 where main_term_search like 'no3%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'no3%')
where main_term_search like 'no3%';

-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'no3%')
where main_term_search like 'no3%';

select * from grf_thesaurus_2013 where main_term_search like 'no3%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'no3%';  
select * from grf_thesaurus_2013 where main_term_search='nitrate ion';
select * from grf_thesaurus@eia where main_term_search='nitrate ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='nitrate ion')
where main_term_search='nitrate ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='nitrate ion';
select * from grf_thesaurus@eia where main_term_search='nitrate ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='nitrate ion')
where main_term_search='nitrate ion';


------------------- 12

select * from grf_thesaurus_2013 where main_term_search like 'oh>%';
select * from grf_thesaurus@eia where main_term_search like 'oh-%';


update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'oh-%')
where main_term_search like 'oh>%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'oh-%')
where main_term_search like 'oh>%';

select * from grf_thesaurus_2013 where main_term_search like 'oh-%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'oh-%')
where main_term_search like 'oh-%';


-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'oh-%')
where main_term_search like 'oh-%';

select * from grf_thesaurus_2013 where main_term_search like 'oh-%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'oh-%';  
select * from grf_thesaurus_2013 where main_term_search='hydroxyl ion';
select * from grf_thesaurus@eia where main_term_search='hydroxyl ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='hydroxyl ion')
where main_term_search='hydroxyl ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='hydroxyl ion';
select * from grf_thesaurus@eia where main_term_search='hydroxyl ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='hydroxyl ion')
where main_term_search='hydroxyl ion';


-------------------- 13
select * from grf_thesaurus_2013 where main_term_search like 'uo<2%';
select * from grf_thesaurus@eia where main_term_search like 'uo2%';

update grf_thesaurus_2013
set main_term_display=
(select main_term_display from grf_thesaurus@eia where main_term_search like 'uo2%')
where main_term_search like 'uo<2%';


update grf_thesaurus_2013
set main_term_search=
(select main_term_search from grf_thesaurus@eia where main_term_search like 'uo2%')
where main_term_search like 'uo<2%';

select * from grf_thesaurus_2013 where main_term_search like 'uo2%';

update grf_thesaurus_2013
set use_terms=
(select use_terms from grf_thesaurus@eia where main_term_search like 'uo2%')
where main_term_search like 'uo2%';

-------------- update status
update grf_thesaurus_2013
set status=
(select status from grf_thesaurus@eia where main_term_search like 'uo2%')
where main_term_search like 'uo2%';

select * from grf_thesaurus_2013 where main_term_search like 'uo2%';


---------------- upate use_terms

select * from grf_thesaurus_2013 where main_term_search like 'uo2%';  
select * from grf_thesaurus_2013 where main_term_search='uranyl ion';
select * from grf_thesaurus@eia where main_term_search='uranyl ion';

update grf_thesaurus_2013
set scope_notes=
(select scope_notes from grf_thesaurus@eia where main_term_search='uranyl ion')
where main_term_search='uranyl ion';


--------- update leadin_terms
select * from grf_thesaurus_2013 where main_term_search='uranyl ion';
select * from grf_thesaurus@eia where main_term_search='uranyl ion';

update grf_thesaurus_2013
set leadin_terms=
(select leadin_terms from grf_thesaurus@eia where main_term_search='uranyl ion')
where main_term_search='uranyl ion';



----------------------- update leading terms for grf terms related to those 13 records

select * from grf_thesaurus@eia where main_term_search='uranyl ion';

select * from grf_thesaurus_2013 where main_term_search='uranyl ion';

---------------------------------------

select t_id,main_term_search from grf_thesaurus_2013;


