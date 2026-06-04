/* -- QUERY DI PROVA -- */ 

/* nummero di vittorie per utente */ 
select username , count(*) as numero_vittorie
from users join participation on users.username = participation.user_id
where score > 0
group by username; 

/* numero di partite disputate */ 
select username , count(*) as partite_disputate
from users join participation on users.username = participation.user_id
group by username;

/* tempo medio di risposta per utennte */ 
select username , avg(response_time) as tempo_medio_di_risposta
from users join participation on users.username = participation.user_id
group by username;

/* storico delle partite per utente x */
select     
	case
        when p1.user_id = 'player2' then p2.user_id
        else p1.user_id
    end as opponent , 
	m.match_date
from participation p1 join participation p2 on p1.match_id = p2.match_id and p1.user_id < p2.user_id join match m on p1.match_id = m.id
where p1.user_id <> p2.user_id and (p1.user_id = 'player2' or p2.user_id = 'player2');