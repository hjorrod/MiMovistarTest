# MiMovistarTest
Prueba Técnica RandomCo App

Esta app tiene las siguientes funcionalidades:
 - Muestra una lista de usuarios aleatorios. Cada petición al servidor solicita 40 usuarios.
 - Se puede marcar como Favorito cada usuario.
 - Se puede eliminar cada usuario.
 - Si marcas como Favorito o eliminas un usuario y ese usuario es recibido del servidor en próximas peticiones, se mostrará en la lista marcado como Favorito o no se mostrará en la lista, según corresponda.
 - En el buscador superior se puede introducir todo o parcialmente el nombre o el email de un usuario. Cuando finalices la escritura, la lista será filtrada acorde al texto introducido mostrando todas las coincidencias.
 - La lista se puede ordenar por nombre y/o por género utilizando los checkBox de la parte superior.
 - Seleccionando cada usuario se accede al detalle de su información donde se visualizan la información de su localización y la fecha de registro en el sistema.
 
El patrón de diseño empleado es MVVM. La arquitectura está dividida en view-domain-data siguiendo patrón repository.

Para almacenar los usuarios favoritos y eliminados, he utilizado Room almacenando los email de aquellos usuarios seleccionados. Después, en cada petición
de una nueva lista de usuarios aleatorios, compruebo cuáles de los recibidos son favoritos para marcarlos y cuáles fueron eliminados para excluirlos de la lista.

Utilizo LiveData y DataBinding para controlar el flujo y presentación de datos.

Para la carga de imágenes utilizo Picasso. Y koin para inyección de dependencias.

Realizo control de errores. Si ocurre algún fallo durante la petición, se muestra un popup al usuario informando del error. También se controla si no tenemos
lista para mostar al usuario, aparecerá un botón "Load more" para volver a realizar una petición de usuarios al servidor.

En una de las peticiones se detalla que se debe mostrar una lista ordenada por nombre. Esta ordenación la realizo en la capa de dominio, así la capa de datos se podría reutilizar en otro proyecto que consuma el mismo API. Además, ordeno cada lista de usuarios recibida del servidor, pero al usuario sólo se le muestra ordenada la primera vez que accede a la app.

También se pide que la lista no muestre usuarios repetidos. Para ello, he diferenciado los usuarios por email, asumiendo que los email son únicos.

Respecto al botón para cargar más usuarios, comentar que lo he implementado de forma que sólo aparecerá si haces scroll en toda la lista actual. Es decir, al acceder a la app, se cargarán 40 usuarios aleatorios. Si realizas el scroll completo, aparecerá el bootón "Load more" que, si es pulsado, cargará otros 40 y así sucesivamente. Estas nuevas peticiones, se obtienen ordenadas por nombre, pero no se ordena la lista actualmente visible al usuario, si no que los nuevos usuarios son añadidos a continuación de los existentes.

Si el usuario quisiera ordenar por nombre la lista, deberá utilizar el checkBox superior habilitado para ello.

Añadir, que también he implementado dos test para comprobar el buen funcionamiento de dos funciones que operan con listas.

Y por último, en cuanto al diseño en la tablet, comentar que se muestran 2 listas: una con los usuarios recibidos del servidor y otra con los marcados como favoritos. Cuando el usuario marque un usuario como favorito, dicho usuario será eliminado de la lista de usuarios y será añadido en la lista de favoritos. Y cuando desmarque un usuario como favorito de la lista de usuarios favoritos, será eliminado de esta lista y añadido a la lista de usuarios.

En cualquiera de las listas, si se selecciona un usuario, se accederá al detalle del mismo (al igual que en un móvil). Sin embargo, las funcionalidades de buscar, ordenar y eliminar usuarios no han sido implementadas para tablet.
