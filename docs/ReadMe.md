\# CapstoneProject TimelineManager



\## Panoramica

TimelineManager è un'applicazione full-stack Kanban-style per la gestione di progetti, con funzionalità di drag-and-drop fluido per i task, autenticazione tramite JWT e gestione ruoli per singolo progetto (CREATOR, ADMIN, GUEST). Il backend è sviluppato in Spring Boot ed espone REST API protette, mentre il frontend è realizzato in React con React-Bootstrap, garantendo un'interfaccia utente fluida e interattiva.



L'obiettivo principale è permettere la gestione multi-utente di progetti tramite categorie e task ordinabili usando un campo `position`, con supporto per l'upload di immagini tramite Cloudinary e gestione dettagliata dei permessi.





>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



\*\*Porte\*\*: Backend 3001 | Frontend 5173 (Vite)



<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<







=========================== ### Backend



| Tecnologia      | Versione | Ruolo                |

|-----------------|----------|----------------------|

| Spring Boot     |    3.x   | Framework principale |

| Spring Security |    6.x   | JWT + @PreAuthorize  |

| PostgreSQL      |     16   | Database relazionale |

| JPA/Hibernate   |     -    | ORM                  |

| Maven           |     -    | Build tool           |

-----------------------------------------------------







=========================== ### Frontend



| Pacchetto                 | Versione         | Ruolo                |

|---------------------------|------------------|----------------------|

| \*\*React\*\*             | \*\*19.2.0\*\*   | Framework UI         |

| \*\*@dnd-kit/core\*\*     | \*\*6.3.1\*\*    | Drag \& Drop moderno |

| \*\*@dnd-kit/sortable\*\* | \*\*10.0.0\*\*   | Sortable lists       |

| \*\*react-bootstrap\*\*   | \*\*2.10.10\*\*  | UI components        |

| \*\*framer-motion\*\*     | \*\*12.23.24\*\* | Animazioni drag-drop |

| \*\*react-router-dom\*\*  | \*\*7.9.6\*\*    | Routing              |

| \*\*Vite\*\*              | \*\*7.2.2\*\*    | Build tool dev       |

---------------------------------------------------------------







=========================== ### API Calls/Fetches



\#\_PROGETTI\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



GET    	/api/projects                    					# Lista progetti utente + member

GET    	/api/projects/{id}               					# Dettaglio progetto

POST   	/api/projects                    					# Crea progetto

PUT    	/api/projects/{id}               					# Update progetto

DELETE 	/api/projects/{id}              					# Elimina progetto



\#\_CATEGORIE\_(Colonne Kanban)\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



GET    	/api/projects/{projectId}/categories              			# Lista categorie

POST   	/api/projects/{projectId}/categories              			# Crea categoria

GET    	/api/projects/{projectId}/categories/{categoryId}  			# Dettaglio categoria

PUT    	/api/projects/{projectId}/categories/{categoryId}  			# Update categoria (LOGGED)

PATCH  	/api/projects/{projectId}/categories/{categoryId}  			# Partial update

DELETE 	/api/projects/{projectId}/categories/{categoryId} 			# Elimina categoria



\#\_TASK\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



GET    	/api/projects/{projectId}/categories/{categoryId}/tasks      		# Lista task

POST   	/api/projects/{projectId}/categories/{categoryId}/tasks      		# Crea task

GET    	/api/projects/{projectId}/categories/{categoryId}/tasks/{id} 		# Dettaglio task

PUT    	/api/projects/{projectId}/categories/{categoryId}/tasks/{id} 		# Drag-drop UPDATE

DELETE 	/api/projects/{projectId}/categories/{categoryId}/tasks/{id} 		# Elimina task



\#\_TASK\_STATUS\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



POST	/api/projects/{projectId}/tasks/{taskId}/complete			# Marca il task come completato

PATCH	/api/projects/{projectId}/tasks/{taskId}/reopening			# Riapre un task precedentemente completato

PATCH	/api/projects/{projectId}/categories/{categoryId}/tasks/{taskId}/status/{statusId}	

&nbsp;										# Aggiorna lo stato del task (statusId)



\#\_TASK\_Drag\&Drop\_(DnD)\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



POST	/api/projects/{projectId}/categories/{categoryId}/tasks/dnd		# Aggiorna l'ordine dei task dopo drag \& drop

PATCH	/api/projects/{projectId}/tasks/{taskId}/category/{newCategoryId}	# Aggiorna la categoria di un task



\#\_PROJECT\_MEMBER\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



POST	/api/projects/{projectId}/members					# Aggiungi membro progetto

DELETE	/api/projects/{projectId}/members/{userId}				# Rimuovi membro progetto

PUT	/api/projects/{projectId}/members/{userId}/role				# Cambia ruolo membro

GET	/api/projects/{projectId}/members					# Lista membri progetto

GET	/api/projects/{projectId}/tasks						# Tutti task progetto

GET	/api/projects/{projectId}/tasks/assignee/{userId}			# Task per assegnatario \[WIP]



\#\_USERS\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



GET	/api/users								# Lista utenti (paginato) \[WIP]

GET	/api/users/profile							# Profilo utente loggato

GET	/api/users/id/{userId}							# Utente per ID

GET	/api/users/email							# Utente per email

PUT	/api/users/{userId}							# Update profilo

PUT	/api/users/{userId}/password						# Cambia password

DELETE	/api/users/{userId}							# Elimina utente \[WIP]

GET	/api/users/{userId}/projects						# Progetti utente



\#\_AUTH\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



POST   	/api/auth/login    							# {email, password} → JWT

POST   	/api/auth/register 							# Crea utente



\#\_AVATAR\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_



POST	/api/users/{userId}/avatar						# Upload avatar (file multipart, Cloudinary)







=========================== ### ENTITA' \& RELAZIONI



\#\_ACTIVITY-LOG



| Campo               	| Tipo                  | Descrizione                                 	| Constraints           	      |

|-----------------------|-----------------------|-----------------------------------------------|-------------------------------|

| `activityId`        	| Long (PK)             | Identificativo univoco attività              	| AUTO\_INCREMENT             	|

| `activityType`      	| ActivityTypeENUM      | Tipo di attività (Enum, es. CREATE, DELETE) 	| NOT NULL             	       	|

| `activityDescription` | String (TEXT)        	| Descrizione dettagliata dell'attività        	| NOT NULL             	      	|

| `timestamp`         	| LocalDateTime         | Data e ora dell'attività                      | NOT NULL, CreationTimestamp 	|



Relazioni



| Campo      		        | Tipo relazione       	| Entità associata 				                      | Tipo Fetch           		      | Nullable | Note 		                	|

|-----------------------|-----------------------|-----------------------------------------------|-------------------------------|----------|----------------------------|

| `user`     	         	| @ManyToOne           	| User             				                      | EAGER                		      | NO       | Utente autore attività    	|

| `project`  		        | @ManyToOne           	| Project          				                      | LAZY                 		      | SÌ       | Progetto associato (opz.)  |

| `task`     		        | @ManyToOne           	| Task             				                      | LAZY                 		      | SÌ       | Task associato (opz.)     	|





\#\_CATEGORY



| Campo               	| Tipo                  | Descrizione                                 	| Constraints           	|

|-----------------------|-----------------------|-----------------------------------------------|--------------------------|

| `categoryId`       	  | Long (PK)		          | Identificativo univoco categoria     		      | AUTO\_INCREMENT       	|

| `categoryName`     	  | String   		          | Nome categoria (es. "To Do")        		      | OPTIONAL           	  	|

| `categoryColor`    	  | String   		          | Colore CSS per UI (es. "#FF6B6B")    		      | OPTIONAL             		|

| `isDefaultInitial` 	  | Boolean  		          | Categoria predefinita iniziale       		      | NOT NULL, DEFAULT true 	|

| `position`         	  | int      		          | Ordine visualizzazione colonne       		      | NOT NULL             		|



Relazioni



| Campo      		| Tipo relazione       	| Entità associata 				| Tipo Fetch      | Nullable | JSON Handling		  |

|---------------|-----------------------|-------------------------|-----------------|----------|--------------------|

| `project` 		| @ManyToOne           	| Project          				| LAZY       			| NO	     | @JsonIgnore       	|

| `tasks`   		| @ManyToMany (mappedBy)| Task          				  | -          			| -        | @JsonBackReference |





\#\_PROJECT



| Campo               	| Tipo                  | Descrizione                                 	| Constraints           	  |

|-----------------------|-----------------------|-----------------------------------------------|---------------------------|

| `projectId`        	  | Long (PK)     	      | ID univoco progetto                  		      | AUTO\_INCREMENT       	  |

| `projectName`      	  | String        	      | Nome progetto                        		      | OPTIONAL             	    |

| `projectDescription`	| String (TEXT)		      | Descrizione progetto                 		      | OPTIONAL             		  |

| `creationDate`     	  | LocalDateTime 	      | Data creazione (auto)                		      | NOT NULL, updatable=false |

| `expiryDate`          | LocalDate     	      | Data scadenza progetto               		      | OPTIONAL             		  |

| `taskCount`        	  | int           	      | \*\*@Transient\*\* - Conta task (calcolato) 	| -                  		    |



Relazioni



| Campo      		    | Tipo relazione       	| Entità associata 				  | Tipo Fetch           		| Cascade/Orphan | JSON Handling 	|

|-------------------|-----------------------|---------------------------|-------------------------|----------------|----------------|

| `categories`  	  | @OneToMany (mappedBy) | Category          				| DEFAULT        		      | ALL/YES        | @JsonIgnore   	|

| `creator`     	  | @ManyToOne            | User              				| \*\*EAGER\*\*      		  | -              | -             	|

| `tasks`       	  | @OneToMany (mappedBy) | Task             			 	  | \*\*LAZY\*\*       		  | ALL/YES        | -             	|

| `comments`    	  | @OneToMany (mappedBy) | Comment           				| \*\*LAZY\*\*       		  | ALL/YES        | -             	|

| `activities`  	  | @OneToMany (mappedBy) | Activity\_Log      				| \*\*LAZY\*\*       		  | ALL/YES        | -             	|

| `projectMembers`	| @OneToMany (mappedBy) | ProjectMember     				| \*\*EAGER\*\*      		  | ALL/YES        | @JsonIgnore   	|





\#\_PROJECTMEMBER



| Campo               	| Tipo                  | Descrizione                                 	| Constraints           	   |

|-----------------------|-----------------------|-----------------------------------------------|----------------------------|

| `projectMemberId`  	  | Long (PK)     	      | ID univoco membership                		      | AUTO\_INCREMENT       	   |

| `creationDate`     	  | LocalDateTime 	      | Data creazione membership (auto)     		      | NOT NULL, updatable=false  |



Relazioni



| Campo      		| Tipo relazione       	| Entità associata 				| Tipo Fetch          | Nullable     |

|---------------|-----------------------|-------------------------|---------------------|--------------|

| `project` 		| @ManyToOne 	 	        | Project          				| \*\*EAGER\*\*  			| \*\*NO\*\*   |

| `user`    		| @ManyToOne  		      | User             				| \*\*EAGER\*\*  			| \*\*NO\*\*   |

| `role`    		| @ManyToOne  		      | User\_Role        			| \*\*EAGER\*\*  			| \*\*NO\*\*   |





\#\_TASK



| Campo               	| Tipo                  | Descrizione                             | Constraints           	|

|-----------------------|-----------------------|-----------------------------------------|-------------------------|

| `taskId`           	  | Long (PK)             | ID univoco task                      		| AUTO\_INCREMENT	       	|

| `taskTitle`        	  | String                | Titolo task                          		| OPTIONAL             		|

| `taskDescription`  	  | String                | Descrizione task                     		| OPTIONAL             		|

| `taskPriority`     	  | TaskPriorityENUM      | Priorità (LOW, MEDIUM, HIGH)        	 	| OPTIONAL             		|

| `createdAt`        	  | LocalDate             | Data creazione (auto)                		| NOT NULL             		|

| `updatedAt`        	  | LocalDate             | Data ultimo aggiornamento (auto)     		| NOT NULL             		|

| `isCompleted`      	  | Boolean               | Flag completamento                   		| OPTIONAL             		|

| `completedAt`      	  | LocalDate             | Data completamento                   		| OPTIONAL             		|

| `taskExpiryDate`   	  | LocalDate             | Scadenza task                        		| OPTIONAL             		|

| `position`         	  | \*\*int\*\*           | Ordine drag \& drop                   	| NOT NULL             		|



Relazioni



| Campo          	   | Tipo relazione       | Entità associata   | Fetch Type     | Nullable   | JSON Handling		      |

|--------------------|----------------------|--------------------|----------------|------------|------------------------|

| `project`      	   | @ManyToOne           | Project            | \*\*EAGER\*\*  | \*\*NO\*\* | @JsonIgnore		        |

| `status`       	   | @ManyToOne           | Task\_Status       | \*\*EAGER\*\*  |  SÌ        | @JsonIgnore 		        |

| `creator`      	   | @ManyToOne           | User               | \*\*EAGER\*\*  | \*\*NO\*\* | @JsonIgnore 		        |

| `lastModifiedBy`	 | @ManyToOne           | User               | DEFAULT        |  SÌ        | @JsonIgnore 		        |

| `assignees`    	   | @OneToMany (mappedBy)| Task\_Assignee     | \*\*LAZY\*\*   |  -         | @JsonIgnore 		        |

| `comments`     	   | @OneToMany (mappedBy)| Comment            | \*\*LAZY\*\*   |  -         | @JsonIgnore 		        |

| `activities`   	   | @OneToMany (mappedBy)| Activity\_Log      | \*\*LAZY\*\*   |  -         | @JsonIgnore 		        |

| `categories`   	   | \*\*@ManyToMany\*\*  | Category           | -              |  -         | @JsonManagedReference 	|





\#\_TASK\_ASSIGNEE



| Campo               	| Tipo                  | Descrizione                                 	| Constraints           	    |

|-----------------------|-----------------------|-----------------------------------------------|-----------------------------|

| `taskAssigneesId` 	  | Long (PK)     	      | ID univoco assegnazione task-utente  		      | AUTO\_INCREMENT       		  |

| `creationDate`    	  | LocalDate     	      | Data di assegnazione (auto)           	      | NOT NULL, updatable=false 	|



Relazioni



| Campo     | Tipo relazione       | Entità associata   | Fetch Type     | Nullable     |

|-----------|----------------------|--------------------|----------------|--------------|

| `task` 		| @ManyToOne           | Task               | \*\*EAGER\*\*  | \*\*NO\*\*   |

| `user` 		| @ManyToOne           | User               | \*\*EAGER\*\*  | \*\*NO\*\*   |





\#\_TASK\_STATUS



| Campo               	| Tipo                  | Descrizione                             | Constraints           	|

|-----------------------|-----------------------|-----------------------------------------|-------------------------|

| `taskStatusId` 	      | Long (PK)     	      | ID univoco stato task                		| AUTO\_INCREMENT         |

| `statusName`   	      | TaskStatusENUM	      | Nome stato (Enum, unico)             		| UNIQUE, NOT NULL        |

| `orderIndex`          | Integer       	      | Indice per ordinamento visuale       		| NOT NULL                |



Relazioni



| Campo          	| Tipo relazione       | Entità associata   | Fetch Type     |

|-----------------|----------------------|--------------------|----------------|

| `tasks`		      | @OneToMany           | Task               | \*\*LAZY\*\*   |





\#\_USER



| Campo               	| Tipo            | Descrizione                             | Constraints           	          |

|-----------------------|-----------------|-----------------------------------------|-----------------------------------|

| `userId`         	    | Long (PK)   		| ID univoco utente                    		| AUTO\_INCREMENT              	    |

| `name`           	    | String      		| Nome utente                          		| NOT NULL, max 50            	    |

| `surname`             | String      		| Cognome utente                       		| NOT NULL, max 50            	    |

| `nickname`       	    | String      		| Nickname univoco                     		| \*\*UNIQUE\*\*, NOT NULL, max 50	|

| `email`          	    | String      		| Email univoca                        		| \*\*UNIQUE\*\*, NOT NULL, max 50	|

| `password`       	    | String      		| Password hash (BCrypt)               		| NOT NULL, max 100           	    |

| `creationDate`   	    | LocalDate   		| Data creazione (auto)                		| NOT NULL                    	    |

| `avatarUrl`      	    | String      		| URL avatar Cloudinary                		| OPTIONAL                    	    |



Relazioni



| Campo             | Tipo relazione       | Entità associata     | Fetch Type     | JSON Handling |

|-------------------|----------------------|----------------------|----------------|---------------|

| `createdProjects` | @OneToMany (mappedBy)| Project              | \*\*LAZY\*\*   | @JsonIgnore   |

| `createdTasks`    | @OneToMany (mappedBy)| Task                 | \*\*LAZY\*\*   | @JsonIgnore   |

| `assignedTasks`   | @OneToMany (mappedBy)| Task\_Assignee       | \*\*LAZY\*\*   | @JsonIgnore   |

| `comments`        | @OneToMany (mappedBy)| Comment              | \*\*LAZY\*\*   | @JsonIgnore   |

| `activities`      | @OneToMany (mappedBy)| Activity\_Log        | \*\*LAZY\*\*   | @JsonIgnore   |

| `notifications`   | @OneToMany (mappedBy)| Email\_Notification  | \*\*LAZY\*\*   | @JsonIgnore   |

| `projectMembers`  | @OneToMany (mappedBy)| ProjectMember        | \*\*LAZY\*\*   | @JsonIgnore   |





\#\_USER\_ROLE



| Campo               	| Tipo                  | Descrizione                             | Constraints           	|

|-----------------------|-----------------------|-----------------------------------------|-------------------------|

| `roleId`  		        | Long (PK)     	      | ID univoco ruolo                     		| AUTO\_INCREMENT       	|

| `roleName`		        | RoleNameENUM  	      | Nome ruolo (CREATOR, ADMIN, GUEST)   		| NOT NULL             		|



Relazioni



| Campo            | Tipo relazione       | Entità associata | Fetch Type   | JSON Handling |

|------------------|----------------------|------------------|--------------|---------------|

| `projectMembers` | @OneToMany (mappedBy)| ProjectMember    | \*\*LAZY\*\* | @JsonIgnore   |







=========================== ### AUTHORITIES LOGIC



| Ruolo   | VIEW  | MODIFY | CREATE/DELETE |

|---------|-------|--------|---------------|

| CREATOR |  ✅  |   ✅   |      ✅      |

| ADMIN   |  ✅  |   ✅   |      ❌      |

| GUEST   |  ✅  |   ❌   |      ❌      |







=========================== ### ENUMS



| Nome Enum               | Valori                                       							                                                 | Utilizzo Principale             |

|------------------------ |------------------------------------------------------------------------------------------------------------|---------------------------------|

| `ProjectPermissionENUM` | VIEW, MODIFY, ADMIN\_ACTIONS, CREATOR\_ACTIONS 							                                               | Permessi su progetti e risorse  |

| `RoleNameENUM`          | CREATOR, ADMIN, GUEST                         							                                               | Ruoli utente per progetto       |

| `TaskPriorityENUM`      | VERY\_LOW, LOW, MEDIUM, HIGH, CRITICAL        							                                               | Priorità task                   |

| `TaskStatusENUM`        | TO\_DO, IN\_PROGRESS, IN\_REVIEW, UNDER\_TESTING, PAUSED, WAITING\_FEEDBACK, BLOCKED, CANCELLED, COMPLETED | Stato di avanzamento task       |



---



\*\*Nota:\*\* Questi enums rappresentano il cuore della logica di permessi, ruoli, priorità e stati task nel sistema




=========================== ### DRAWSQL



Per una ulteriore visibilità delle funzionalità, controllare il DrawSQL nella cartella principale \\CapstoneProject\_TimelineManager\\docs\\TimelineManager.png


















Link per Repository GitHub parte FRONT-END:

https://github.com/Dade1991/CapstoneProject_TimelineManager_FE


---



Per qualsiasi domanda, proposta di miglioria, scoperta di bug, commenti, o altro:





\*\*Davide Braghi\*\*  

Full-Stack Developer / Project Manager  

📧 dade91@msn.com  

🔗 \[linkedin.com/in/davidebraghi](https://linkedin.com/in/davide-braghi-99024487)



---


