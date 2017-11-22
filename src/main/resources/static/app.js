var app = (function () {

    var nombreJugador = "NN";

    var stompClient = null;
    var gameid = 0;


    return {
        //Datos del jugador
        loadUser: function (userid) {
            $.get("/hangmangames/" + userid,
                    function (data) {
                        alert(JSON.stringify(data));
                        nombreJugador = data.name;
                        $("#ultimoPuntaje").html("<div id='ultimoPuntaje'>Ultimo puntaje: "+data.scores[data.scores.length-1]+"</div>")
                        $("#photoUser").html("<div id = 'photoUser'><img src=" + data.photoUrl + "></img></div>");
                        $("#nameUser").html("<div id= 'nameUser'> Nombre: " + data.name + "</div>");
                    }
            ).fail(
                    function (data) {
                        alert(data["responseText"]);
                    }
            );
        },
        loadScores: function(score){
            $.get("/hangmangames/scores/" + score, 
                    function(data){
                        var stringData=""
                        for(var i=0;i<data.length;i++){
                                stringData+="<label>Score: "+data[i]+"<label>"
                                }
                        $("#scores").html("<div id='scores'>Usuarios con puntajes mayores a 100:"+
                                stringData
                                +"</div>");
                    }
            ).fail(
                    function (data) {
                        alert(data["responseText"]);
                    }
            );       
        },
        loadWord: function (gameid1) {

            gameid = gameid1;

            $.get("/hangmangames/" + gameid + "/currentword",
                    function (data) {
                        $("#palabra").html("<h1>" + data + "</h1>");
                    }
            ).fail(
                    function (data) {
                        alert(data["responseText"]);
                    }
            );
        }
        ,
        wsconnect: function () {
            var socket = new SockJS('/stompendpoint');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                //subscriptions
                stompClient.subscribe("/topic/wupdate." + gameid, function (eventbody) {
                    $("#palabra").html("<h1>" + eventbody.body + "</h1>");
                });

                stompClient.subscribe("/topic/winner." + gameid, function (eventbody) {
                    $("#winner").html("<div id='winner'> Ganador: " + eventbody.body + "</div>");
                    $("#estado").html("<div id='estado'> Estado: Finalizado </div>");
                });
            });
        }
        ,
        sendLetter: function () {
            
            var id = gameid;

            var hangmanLetterAttempt = {letter: $("#caracter").val(), username: nombreJugador};

            console.info("Gameid:" + gameid + ",Sending letter:" + JSON.stringify(hangmanLetterAttempt));

            var pos = function () {
                var r = jQuery.ajax({
                    url: "/hangmangames/" + id + "/letterattempts",
                    type: "POST",
                    data: JSON.stringify(hangmanLetterAttempt),
                    contentType: "application/json; charset=utf-8"
                });
                return r;
            };

            pos().then(function (tmp) {
                stompClient.send("/topic/wupdate." + id, {}, tmp);        
            });

        },
        sendWord: function () {
            
            var id = gameid;

            var hangmanWordAttempt = {word: $("#adivina").val(), username: nombreJugador};
            
            console.info("Gameid:" + gameid + ",Sending word:" + JSON.stringify(hangmanWordAttempt));

            var pos = function () {
                var r = jQuery.ajax({
                    url: "/hangmangames/" + id + "/wordattempts",
                    type: "POST",
                    data: JSON.stringify(hangmanWordAttempt),
                    contentType: "application/json; charset=utf-8"
                });
                return r;
            };
            
            pos().then(function (win) {
                if(win){
                    stompClient.send("/topic/winner." + id, {}, nombreJugador);
                    $.get("/hangmangames/" + id + "/currentword",
                    function (data) {
                        stompClient.send("/topic/wupdate." + id, {}, data); 
                    });
                }
            });
        }
    };

})();

