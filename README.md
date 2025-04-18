you can run the program from within Game main method.
The game offers few functionalities: regular chess, ability to simulate game using PGN format and run multiple PGN format files concurrently to check if their annotations are valid(buggy). 
My Process:
First i started with fixing logic and implementing so of the unimplemented functionalities such as en passant,fixing pawn capturing own piece, knight capturing own piece,added castling,pieces losing their "checking" ability.
I applied MVC meaning I separated Controller and View to their own packages.
during the process i added unit tests.
after logic was satisfactory i started working on PGN format handling which is now accesable in startmenu(simulation is moved forward with Mouse Click) 
i adapted left Clicking from already working chess logic to use wMoves hashmap  to get pieces that can go to destionation of pgn move and handled ambiguitys with piece type and specializers.
