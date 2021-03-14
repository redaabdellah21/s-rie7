package exo7;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;


public class ActorsAndMovies {

    public static void main(String[] args) {

        ActorsAndMovies actorsAndMovies = new ActorsAndMovies();
        System.out.println("\n Question 1 \n");
        System.out.println("Nombre des films = " + 
        		actorsAndMovies.readMovies()
        					   .size());
        
        System.out.println("\n Question 2 \n");
        System.out.println("Nombre des acteurs = " + 
        		actorsAndMovies.readMovies()
        					   .stream()
        					   .distinct()
        					   .flatMap(s -> s.actors().stream())
        					   .collect(Collectors.toSet())
        					   .size());

        System.out.println("\n Question 3 \n");
        System.out.println("Nombre des année = " + 
        		actorsAndMovies.readMovies()
        					   .stream()
        					   .distinct()
        					   .map(Movie::releaseYear)
        					   .collect(Collectors.toSet())
        					   .size());
        System.out.println("\n Question 4 \n");
        System.out.println("L'année du film le plus récent = " + 
        		actorsAndMovies.readMovies()
        					   .stream()
        					   .map(Movie::releaseYear)
        					   .max(Integer::compare)
        					   .orElseThrow());
        System.out.println("L'année du film le plus vieux = " + 
        		actorsAndMovies.readMovies()
        					   .stream()
        					   .map(Movie::releaseYear)
        					   .min(Integer::compare)
        					   .orElseThrow());
        
        System.out.println("\n Question 5 \n");
        System.out.println("L'année qui a plus des films= " + 
        		actorsAndMovies.readMovies()
				   			   .stream()
				   			   .collect(Collectors.groupingBy(Movie::releaseYear, Collectors.counting())).entrySet()
				   			   			.stream()
				   			   			.max(Comparator.comparing(Map.Entry :: getValue))
				   			   			.orElseThrow()
);
        System.out.println("\n Question 6 \n");
        String mostActeurs = actorsAndMovies.readMovies()
					   .stream()
					   .max(Comparator.comparing(s->s.actors().size()))
					   .orElseThrow()
					   .title();
		System.out.println("Le film avec le plus grand nombre d'acteurs = " + 
        		mostActeurs);
        int nbactors = actorsAndMovies.readMovies()
					   .stream()
					   .max(Comparator.comparing(s->s.actors().size()))
					   .orElseThrow()
					   .actors().size();
		System.out.println("Le nombre d'acteurs = " + 
        		nbactors);
        
        System.out.println("\n Question 7 \n");
        Entry<Actor, Long> mostFilmsInOneYear = actorsAndMovies.readMovies()
		   			   .stream()
		   			   .flatMap(s -> s.actors().stream())
		   			   .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
		   			   		.entrySet().stream()
		   			   		.max(Comparator.comparing(Map.Entry::getValue))
		   			   		.orElseThrow();
		System.out.println("L'acteur avec le plus de film en une année = "+
        		mostFilmsInOneYear);
        
        
        System.out.println("\n Question 8 \n");
   	Entry<Actor, Long> acteurInOneYear = actorsAndMovies.readMovies()
					   .stream()
					   .collect(   	 
							    Collectors.collectingAndThen(
							    							Collectors.flatMapping(
							    													movie -> movie.actors().stream() ,
							    													Collectors.groupingBy(
							    															Function.identity() ,
							    															Collectors.counting()
							    															)
							    												  ) ,
							    							m-> m.entrySet().stream()
							    							.max(Comparator.comparing(Map.Entry::getValue))
							    							.orElseThrow()
							    							)
							    );
	System.out.println("L'acteur avec le plus de film en une année (avec le comparateur) =  :"+ 
   				acteurInOneYear);
   		System.out.println("\n Question 9 \n");
   		//a
   	
   		Comparator <Actor> c = Comparator.comparing((Actor a) -> a.lastName).thenComparing((Actor a) -> a.firstName);
   		
   		//b
   		System.out.println("\n Question 9-b \n");
   		Actor evans= new Actor("Evans", "Chris");
   		
    	BiFunction<Stream<Actor>, Actor,Stream< Map.Entry<Actor, Actor>>> pairesActeurs = (Stream<Actor> t, Actor u) -> 
    							t.filter(s-> c.compare(s,u)<0) // nous allons mettre le signe < pour adapter le fonctionnement aus derniers questions, sinon il suffit de mettre !=
    							.map(act -> Map.entry(act, u));
    	
    	pairesActeurs.apply(actorsAndMovies.readMovies().stream().flatMap(s -> s.actors().stream().limit(1))
    			 		, evans).forEach( acteur -> System.out.println("("+acteur.getKey().lastName + " , " + acteur.getValue().lastName+")"));
    	
    	//c
    	System.out.println("\n Question 9-c \n");
    	Movie movie =actorsAndMovies.readMovies().stream().findFirst().get();
    	
    	Function <Movie, Stream <Actor>> acteursMovie= (Movie m) ->m.actors().stream();
    	acteursMovie.apply(movie).forEach( acteurs -> System.out.println(acteurs));
    	
    	//d
    	System.out.println("\n Question 9-d \n");
    	BiFunction<Movie, Actor, Stream<Map.Entry<Actor, Actor>>> pairesMovie = (Movie m, Actor a) -> pairesActeurs.apply(acteursMovie.apply(m), a);
    	pairesMovie.apply(movie,evans)
    			   .forEach( acteur -> System.out.println("("+acteur.getKey().lastName + " , " + acteur.getValue().lastName+")"));
    	
    	//e
    	System.out.println("\n Question 9-e \n");
    	Function<Movie, Stream<Map.Entry<Actor, Actor>>> pairesOneMovie = (Movie m) -> 
    		acteursMovie.apply(m).flatMap(acteur -> pairesMovie.apply(m,acteur));
    	pairesOneMovie.apply(movie).forEach( acteur -> System.out.println("("+acteur.getKey().lastName + " , " + acteur.getValue().lastName+")"));
    	
    	//f
    	System.out.println("\n Question 9-f \n");
    	System.out.println("Le nombre des paires dans le fichiers :"+actorsAndMovies.readMovies()
		   .stream()
		   .flatMap(film -> pairesOneMovie.apply(film))
		   .count());
    	
    	System.out.println("Le nombre des paires uniques dans le fichiers :"+actorsAndMovies.readMovies()
		   .stream()
		   .flatMap(film -> pairesOneMovie.apply(film))
		   .distinct()
		   .count());
    	//g
    	System.out.println("\n Question 9-g \n");
    	Entry<Entry<Actor, Actor>, Long> mostAtAll = actorsAndMovies.readMovies()
		   .stream()
		   .flatMap(film -> pairesOneMovie.apply(film))
		   .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
		   .entrySet()
		   .stream()
		   .max(Comparator.comparing(Entry:: getValue))
		   .orElseThrow();
		System.out.println("Les deux acteurs qui ont joués le plus :"+mostAtAll);
    	
    	System.out.println("\n Question 10 \n");
    	Collector<Movie, Integer, Entry<Entry<Actor, Actor>, Long>> collectpaires =
    	Collectors.collectingAndThen(
    			Collectors.flatMapping(f -> pairesOneMovie.apply(f), 
    					Collectors.groupingBy(Function.identity(),Collectors.counting()))
    			,
    			map -> map.entrySet().stream()
    			.max(Comparator.comparing(Entry:: getValue))
    			.orElseThrow());
    	Entry<Integer, Entry<Entry<Actor, Actor>, Long>> mostInOneYear = actorsAndMovies.readMovies()
		   .stream().collect(Collectors.groupingBy(Movie::releaseYear,collectpaires))
		   .entrySet().stream()
		   .max(Comparator.comparing(ent -> ent.getValue().getValue()))
		   .orElseThrow();
		System.out.println("Les deux acteurs qui ont joués le plus en une seules année :"+mostInOneYear);
    	
    	
    }

    public Set<Movie> readMovies() {

        Function<String, Stream<Movie>> toMovie =
                line -> {
                    String[] elements = line.split("/");
                    String title = elements[0].substring(0, elements[0].lastIndexOf("(")).trim();
                    String releaseYear = elements[0].substring(elements[0].lastIndexOf("(") + 1, elements[0].lastIndexOf(")"));
                    if (releaseYear.contains(",")) {
                        int index=releaseYear.indexOf(",");
                        releaseYear=releaseYear.substring(0,index);
                    }
                    Movie movie = new Movie(title, Integer.valueOf(releaseYear));


                    for (int i = 1; i < elements.length; i++) {
                        String[] name = elements[i].split(", ");
                        String lastName = name[0].trim();
                        String firstName = "";
                        if (name.length > 1) {
                            firstName = name[1].trim();
                        }

                        Actor actor = new Actor(lastName, firstName);
                        movie.addActor(actor);
                    }
                    return Stream.of(movie);
                };

        try (FileInputStream fis = new FileInputStream("files\\movies-mpaa.txt.gz");
             GZIPInputStream gzis = new GZIPInputStream(fis);
             InputStreamReader reader = new InputStreamReader(gzis);
             BufferedReader bufferedReader = new BufferedReader(reader);
             Stream<String> lines = bufferedReader.lines();
        ) {

            return lines.flatMap(toMovie).collect(Collectors.toSet());

        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

        return Set.of();
    }
}
