/* -- INSERT DI TEST -- */ 
	insert into users ('username' , 'password' , 'type') values ('admin' , 'admin' , 'administrator'); 

	insert into users ('username' , 'password') values ('player1' , 'player1');
	insert into users ('username' , 'password') values ('player2' , 'player2');
	insert into users ('username' , 'password') values ('player3' , 'player3');




	insert into texts ('title' , 'lenght' , 'path') values ('i promessi sposi' , 1000 , 'data/ipromessisposi.txt');
	insert into texts ('title' , 'lenght' , 'path') values ('la divina commedia' , 500 , 'data/divinacommedia.txt');



	insert into match ('match_date' , 'text_id') values (current_timestamp , 'i promessi sposi');
	insert into match ('match_date' , 'text_id') values (current_timestamp , 'i promessi sposi');
	insert into match ('match_date' , 'text_id') values (current_timestamp , 'la divina commedia');



	insert into participation values ('player1' , 1 , 100 , 15.2);
	insert into participation values ('player2' , 1 , 0 , 30);
	
	insert into participation values ('player1' , 2 , 0 , 30);
	insert into participation values ('player3' , 2 , 75 , 5.6);
	
	insert into participation values ('player2' , 3 , 0 , 20);
	insert into participation values ('player3' , 3 , 45 , 8.45);
