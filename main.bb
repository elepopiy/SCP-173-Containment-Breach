; ============================================================
; SCP-173
; BLITZ3D x95
;
; TEXTURE / UV FIXED EDITION
;
; - Wall.jpg gerilmez
; - Ground.jpg gerilmez
; - Grass.jpg gerilmez
; - ScaleTexture YOK
; - Gercek UV tile
; - Ground normal yonleri duzgun
; - Gereksiz ic yuzler yok
; - Duvar kesismelerinde daha az z-fighting
; - "_" SATIR DEVAM ISARETI YOK
; ============================================================

AppTitle "SCP-173"

; ============================================================
; GRAPHICS
; ============================================================

Const SCREEN_W = 1920
Const SCREEN_H = 1080
Const SCREEN_DEPTH = 32

Global FULLSCREEN = 0

Graphics3D SCREEN_W,SCREEN_H,SCREEN_DEPTH,FULLSCREEN
SetBuffer BackBuffer()

; ============================================================
; TEXTURES
; ============================================================

Global WallTexture
Global GroundTexture
Global GrassTexture

WallTexture = LoadTexture("GFX\Wall.jpg")
GroundTexture = LoadTexture("GFX\Ground.jpg")
GrassTexture = LoadTexture("GFX\Grass.jpg")

Const WALL_TILE_SIZE# = 2.0
Const GROUND_TILE_SIZE# = 2.0
Const GRASS_TILE_SIZE# = 4.0

; ============================================================
; WORLD
; ============================================================

Const WORLD_MIN_X# = -28.0
Const WORLD_MAX_X# = 28.0
Const WORLD_MIN_Z# = -12.0
Const WORLD_MAX_Z# = 100.0

; ============================================================
; PLAYER
; ============================================================

Const PLAYER_HEIGHT# = 1.65
Const PLAYER_RADIUS# = .38

Const PLAYER_WALK_SPEED# = 4.0
Const PLAYER_RUN_SPEED# = 8.0

Const MOUSE_SENS# = .10

Global Player
Global Camera

Global CamYaw# = 0
Global CamPitch# = 0

Global PlayerDead = 0

Global DeathTime = 0
Const RESTART_DELAY = 3000

; ============================================================
; FOOTSTEP
; ============================================================

Global WalkSound
Global RunSound

Global FootstepChannel
Global FootstepMode = 0

WalkSound = LoadSound("SFX\Walk.mp3")
RunSound = LoadSound("SFX\Run.mp3")

; ============================================================
; MUSIC
; ============================================================

Global OstSound
Global ThemeSound

Global MusicChannel
Global CurrentMusic = 0

OstSound = LoadSound("SFX\Ost.mp3")
ThemeSound = LoadSound("SFX\Theme.mp3")

; ============================================================
; E
; ============================================================

Global EPressed = 0

; ============================================================
; SCP
; ============================================================

Const SCP_SCALE# = .15

Const SCP_BASE_SPEED# = 500.0
Const SCP_BLINK_SPEED# = 500.0
Const SCP_BLINK_MAX_DISTANCE# = 10.0

Const SCP_RADIUS# = .35
Const SCP_ATTACK_DISTANCE# = .85

Global SCP_GROUND_Y# = .05

Global SCPPicked = 0
Global SCPPlaced = 0
Global SCPAlive = 0

Global SCP173
Global SCPTexture
Global SCPUnpaintedTexture

Global SCPState = 0
Global SCPAnimTimer = 0

Global SCPPathTimer = 0
Global SCPPathIndex = 0
Global SCPPathCount = 0

Const SCP_PATH_REFRESH = 180

; ============================================================
; BLINK
; ============================================================

Const BLINK_DURATION = 300

Global BlinkActive = 0
Global BlinkStartTime = 0

Global BlinkSCPStartX# = 0
Global BlinkSCPStartZ# = 0
Global BlinkSCPDistance# = 0

; ============================================================
; STATES
; ============================================================

Const STATE_NORMAL = 0
Const STATE_PICKUP = 1
Const STATE_CARRY = 2
Const STATE_PLACE = 3
Const STATE_MACHINE_READY = 4
Const STATE_ACTIVATE = 5
Const STATE_ACTIVE = 6
Const STATE_ATTACK = 7

; ============================================================
; COLLISION
; ============================================================

Const MAX_OBSTACLES = 500

Dim ObsX#(MAX_OBSTACLES)
Dim ObsZ#(MAX_OBSTACLES)
Dim ObsHX#(MAX_OBSTACLES)
Dim ObsHZ#(MAX_OBSTACLES)

Global ObsCount = 0

; ============================================================
; GATES
; ============================================================

Dim GateLeft(3)
Dim GateRight(3)

Dim GateOpen(3)
Dim GateOffset#(3)

; ============================================================
; CRATE
; ============================================================

Global CrateFloor

Dim CrateBeams(30)

Global CrateCount = 0

; ============================================================
; MACHINE
; ============================================================

Global MachineBody
Global MachineFront
Global MachineLeft
Global MachineRight
Global MachineBack

Global MachinePlatform
Global MachinePanel
Global MachineScreen
Global MachineButton

Global MachineLight
Global AlarmLight

Global MachineReady = 0
Global MachineRunning = 0
Global MachineAnimTimer = 0

; ============================================================
; ATMOSPHERE
; ============================================================

Global Flashlight
Global CreepyLight

; ============================================================
; A*
; ============================================================

Const NAV_CELL# = 1.0

Const NAV_MIN_X# = -30.0
Const NAV_MAX_X# = 30.0

Const NAV_MIN_Z# = -14.0
Const NAV_MAX_Z# = 102.0

Const NAV_W = 61
Const NAV_H = 117

Const NAV_NODES = 7137

Dim NavOpen(NAV_NODES)
Dim NavClosed(NAV_NODES)
Dim NavParent(NAV_NODES)

Dim NavG#(NAV_NODES)
Dim NavH#(NAV_NODES)
Dim NavF#(NAV_NODES)

Dim PathX#(2048)
Dim PathZ#(2048)

; ============================================================
; TIME
; ============================================================

Global LastFrameTime = 0

Global MainNow = 0
Global MainFrameMS = 0
Global MainDT# = 0

Global LastF11 = 0

; ============================================================
; COLLISION
; ============================================================

Function ResetObstacles()

	ObsCount = 0

End Function

Function AddObstacle(x#,z#,hx#,hz#)

	If ObsCount >= MAX_OBSTACLES Then Return

	ObsX#(ObsCount) = x#
	ObsZ#(ObsCount) = z#

	ObsHX#(ObsCount) = hx#
	ObsHZ#(ObsCount) = hz#

	ObsCount = ObsCount + 1

End Function

Function IsStaticBlocked(x#,z#,radius#)

	Local i

	For i = 0 To ObsCount - 1

		If x# > ObsX#(i)-ObsHX#(i)-radius# Then

			If x# < ObsX#(i)+ObsHX#(i)+radius# Then

				If z# > ObsZ#(i)-ObsHZ#(i)-radius# Then

					If z# < ObsZ#(i)+ObsHZ#(i)+radius# Then

						Return 1

					EndIf

				EndIf

			EndIf

		EndIf

	Next

	Return 0

End Function

; ============================================================
; GATE
; ============================================================

Function GateBlockedWorld(index,doorZ#,x#,z#,radius#)

	Local leftX#
	Local rightX#

	If GateOpen(index) = 1 Then Return 0

	leftX# = -1.25-GateOffset#(index)
	rightX# = 1.25+GateOffset#(index)

	If x# > leftX#-1.25-radius# Then

		If x# < leftX#+1.25+radius# Then

			If z# > doorZ#-.20-radius# Then

				If z# < doorZ#+.20+radius# Then

					Return 1

				EndIf

			EndIf

		EndIf

	EndIf

	If x# > rightX#-1.25-radius# Then

		If x# < rightX#+1.25+radius# Then

			If z# > doorZ#-.20-radius# Then

				If z# < doorZ#+.20+radius# Then

					Return 1

				EndIf

			EndIf

		EndIf

	EndIf

	Return 0

End Function

; ============================================================
; WORLD BLOCK
; ============================================================

Function WorldBlocked(x#,z#,radius#)

	If IsStaticBlocked(x#,z#,radius#) = 1 Then Return 1

	If GateBlockedWorld(0,-4,x#,z#,radius#) = 1 Then Return 1
	If GateBlockedWorld(1,4,x#,z#,radius#) = 1 Then Return 1
	If GateBlockedWorld(2,12,x#,z#,radius#) = 1 Then Return 1

	Return 0

End Function

; ============================================================
; SCP BLOCK
; ============================================================

Function IsSCPBlockingPlayer(x#,z#,radius#)

	Local dx#
	Local dz#
	Local dist#
	Local combined#

	If SCP173 = 0 Then Return 0

	If SCPState = STATE_PICKUP Then Return 0
	If SCPState = STATE_CARRY Then Return 0

	dx# = x#-EntityX(SCP173,True)
	dz# = z#-EntityZ(SCP173,True)

	dist# = Sqr(dx#*dx#+dz#*dz#)

	combined# = radius#+SCP_RADIUS#

	If dist# < combined# Then Return 1

	Return 0

End Function

; ============================================================
; DECOR CUBE
; ============================================================

Function MakeDecorCube(x#,y#,z#,sx#,sy#,sz#,r,g,b)

	Local e

	e = CreateCube()

	PositionEntity e,x#,y#,z#
	ScaleEntity e,sx#,sy#,sz#
	EntityColor e,r,g,b

	Return e

End Function

; ============================================================
; SOLID CUBE
; ============================================================

Function MakeSolidCube(x#,y#,z#,sx#,sy#,sz#,r,g,b)

	Local e

	e = CreateCube()

	PositionEntity e,x#,y#,z#
	ScaleEntity e,sx#,sy#,sz#
	EntityColor e,r,g,b

	AddObstacle x#,z#,sx#,sz#

	Return e

End Function

; ============================================================
; WALL MESH
;
; Onemli:
; Bir duvar prizmasinin ust ve alt yuzleri texture'lanmiyor.
; Bu yuzler cogu zaman baska bir parcanin icinde kalir.
;
; Böylece:
;   - gereksiz texture katmanlari azalir
;   - duvarlar ic ice girdiginde daha az z-fighting olur
; ============================================================

Function MakeWallCube(x#,y#,z#,sx#,sy#,sz#,r,g,b)

	Local mesh
	Local surf

	Local hx#
	Local hy#
	Local hz#

	Local width#
	Local height#
	Local depth#

	Local uWidth#
	Local uDepth#
	Local vHeight#

	Local v0
	Local v1
	Local v2
	Local v3

	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	hx# = sx#
	hy# = sy#
	hz# = sz#

	width# = sx#*2.0
	height# = sy#*2.0
	depth# = sz#*2.0

	uWidth# = width#/WALL_TILE_SIZE#
	uDepth# = depth#/WALL_TILE_SIZE#
	vHeight# = height#/WALL_TILE_SIZE#

	If uWidth# < .01 Then uWidth# = .01
	If uDepth# < .01 Then uDepth# = .01
	If vHeight# < .01 Then vHeight# = .01

	; ========================================================
	; FRONT -Z
	; OUTWARD NORMAL
	; ========================================================

	v0 = AddVertex(surf,-hx#,-hy#,-hz#,0,vHeight#)
	v1 = AddVertex(surf,-hx#,hy#,-hz#,0,0)
	v2 = AddVertex(surf,hx#,hy#,-hz#,uWidth#,0)
	v3 = AddVertex(surf,hx#,-hy#,-hz#,uWidth#,vHeight#)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	; ========================================================
	; BACK +Z
	; ========================================================

	v0 = AddVertex(surf,hx#,-hy#,hz#,0,vHeight#)
	v1 = AddVertex(surf,hx#,hy#,hz#,0,0)
	v2 = AddVertex(surf,-hx#,hy#,hz#,uWidth#,0)
	v3 = AddVertex(surf,-hx#,-hy#,hz#,uWidth#,vHeight#)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	; ========================================================
	; LEFT -X
	; ========================================================

	v0 = AddVertex(surf,-hx#,-hy#,hz#,0,vHeight#)
	v1 = AddVertex(surf,-hx#,hy#,hz#,0,0)
	v2 = AddVertex(surf,-hx#,hy#,-hz#,uDepth#,0)
	v3 = AddVertex(surf,-hx#,-hy#,-hz#,uDepth#,vHeight#)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	; ========================================================
	; RIGHT +X
	; ========================================================

	v0 = AddVertex(surf,hx#,-hy#,-hz#,0,vHeight#)
	v1 = AddVertex(surf,hx#,hy#,-hz#,0,0)
	v2 = AddVertex(surf,hx#,hy#,hz#,uDepth#,0)
	v3 = AddVertex(surf,hx#,-hy#,hz#,uDepth#,vHeight#)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	; ========================================================
	; UST / ALT YUZLER BILEREK YOK
	; ========================================================

	If WallTexture <> 0 Then

		EntityTexture mesh,WallTexture
		EntityColor mesh,255,255,255

	Else

		EntityColor mesh,r,g,b

	EndIf

	PositionEntity mesh,x#,y#,z#

	AddObstacle x#,z#,sx#,sz#

	UpdateNormals mesh

	Return mesh

End Function

; ============================================================
; WALL DECOR
;
; Burasi duvarin icinde / ustunde duran dekorlar icin.
; Cogu durumda tekrar ikinci bir duvar kaplamasi bindirmemesi
; icin SADECE on yuzu texture'lanir.
; ============================================================

Function MakeWallDecor(x#,y#,z#,sx#,sy#,sz#,r,g,b)

	Local mesh
	Local surf

	Local hx#
	Local hy#
	Local hz#

	Local width#
	Local height#

	Local uWidth#
	Local vHeight#

	Local v0
	Local v1
	Local v2
	Local v3

	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	hx# = sx#
	hy# = sy#
	hz# = sz#

	width# = sx#*2.0
	height# = sy#*2.0

	uWidth# = width#/WALL_TILE_SIZE#
	vHeight# = height#/WALL_TILE_SIZE#

	If uWidth# < .01 Then uWidth# = .01
	If vHeight# < .01 Then vHeight# = .01

	; ON YUZ -Z
	;
	; SADECE TEK YUZ.
	; Boylece duvarin arkasindaki texture katmani
	; tekrar render edilmez.

	v0 = AddVertex(surf,-hx#,-hy#,-hz#,0,vHeight#)
	v1 = AddVertex(surf,-hx#,hy#,-hz#,0,0)
	v2 = AddVertex(surf,hx#,hy#,-hz#,uWidth#,0)
	v3 = AddVertex(surf,hx#,-hy#,-hz#,uWidth#,vHeight#)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	If WallTexture <> 0 Then

		EntityTexture mesh,WallTexture
		EntityColor mesh,255,255,255

	Else

		EntityColor mesh,r,g,b

	EndIf

	PositionEntity mesh,x#,y#,z#

	UpdateNormals mesh

	Return mesh

End Function

; ============================================================
; GROUND
;
; Sadece UST yuzey.
; Triangle sirasi artik YUKARI bakiyor.
; ============================================================

Function MakeGroundDecor(x#,y#,z#,sx#,sy#,sz#,r,g,b)

	Local mesh
	Local surf

	Local u#
	Local v#

	Local v0
	Local v1
	Local v2
	Local v3

	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	u# = (sx#*2.0)/GROUND_TILE_SIZE#
	v# = (sz#*2.0)/GROUND_TILE_SIZE#

	If u# < .01 Then u# = .01
	If v# < .01 Then v# = .01

	; ========================================================
	; TOP
	;
	; DOGRU NORMAL = +Y
	; ========================================================

	v0 = AddVertex(surf,-sx#,sy#,sz#,0,v#)
	v1 = AddVertex(surf,sx#,sy#,sz#,u#,v#)
	v2 = AddVertex(surf,sx#,sy#,-sz#,u#,0)
	v3 = AddVertex(surf,-sx#,sy#,-sz#,0,0)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	If GroundTexture <> 0 Then

		EntityTexture mesh,GroundTexture
		EntityColor mesh,255,255,255

	Else

		EntityColor mesh,r,g,b

	EndIf

	PositionEntity mesh,x#,y#,z#

	UpdateNormals mesh

	Return mesh

End Function

; ============================================================
; GRASS
; ============================================================

Function MakeGrassGround()

	Local mesh
	Local surf

	Local sx#
	Local sy#
	Local sz#

	Local width#
	Local depth#

	Local u#
	Local v#

	Local v0
	Local v1
	Local v2
	Local v3

	sx# = 80.0
	sy# = .35
	sz# = 53.75

	width# = sx#*2.0
	depth# = sz#*2.0

	u# = width#/GRASS_TILE_SIZE#
	v# = depth#/GRASS_TILE_SIZE#

	mesh = CreateMesh()
	surf = CreateSurface(mesh)

	; ========================================================
	; GRASS UST
	; NORMAL +Y
	; ========================================================

	v0 = AddVertex(surf,-sx#,sy#,sz#,0,v#)
	v1 = AddVertex(surf,sx#,sy#,sz#,u#,v#)
	v2 = AddVertex(surf,sx#,sy#,-sz#,u#,0)
	v3 = AddVertex(surf,-sx#,sy#,-sz#,0,0)

	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3

	If GrassTexture <> 0 Then

		EntityTexture mesh,GrassTexture
		EntityColor mesh,255,255,255

	Else

		EntityColor mesh,40,96,38

	EndIf

	PositionEntity mesh,0,-.35,66.25

	UpdateNormals mesh

	Return mesh

End Function

; ============================================================
; BUILDING
; ============================================================

Function CreateBuilding()

	Local floor
	Local ceiling

	floor = MakeGroundDecor(0,-.15,0,7,.15,24,44,46,50)

	ceiling = CreateCube()

	PositionEntity ceiling,0,5.5,0
	ScaleEntity ceiling,7,.15,24
	EntityColor ceiling,38,40,44

	MakeWallCube -7,2.75,0,.15,2.75,24,74,77,84

	MakeWallCube 7,2.75,-8,.15,2.75,4,74,77,84
	MakeWallCube 7,2.75,0,.15,2.75,3,74,77,84
	MakeWallCube 7,2.75,8,.15,2.75,4,74,77,84

	MakeWallCube 0,2.75,-12,7,2.75,.15,74,77,84

	MakeWallCube -4.75,2.75,-4,2.25,2.75,.15,74,77,84
	MakeWallCube 4.75,2.75,-4,2.25,2.75,.15,74,77,84

	MakeWallDecor 0,4.6,-4,4.75,.9,.15,74,77,84

	MakeWallCube -4.75,2.75,4,2.25,2.75,.15,74,77,84
	MakeWallCube 4.75,2.75,4,2.25,2.75,.15,74,77,84

	MakeWallDecor 0,4.6,4,4.75,.9,.15,74,77,84

	MakeWallCube -4.75,2.75,12,2.25,2.75,.15,74,77,84
	MakeWallCube 4.75,2.75,12,2.25,2.75,.15,74,77,84

	MakeWallDecor 0,4.6,12,4.75,.9,.15,74,77,84

	CreepyLight = CreateCeilingLight(-3,-8)

	CreateCeilingLight 3,-1
	CreateCeilingLight -3,7
	CreateCeilingLight 3,10

End Function

; ============================================================
; CEILING LIGHT
; ============================================================

Function CreateCeilingLight(x#,z#)

	Local lamp
	Local light

	lamp = CreateCube()

	PositionEntity lamp,x#,5.15,z#
	ScaleEntity lamp,1.2,.05,.35
	EntityColor lamp,200,205,210

	light = CreateLight(2)

	PositionEntity light,x#,4.7,z#
	LightRange light,8
	LightColor light,150,156,168

	Return light

End Function

; ============================================================
; SIDE ROOM
; ============================================================

Function CreateSideRoom()

	MakeGroundDecor 10,-.15,0,3,.15,3,46,48,52

	MakeDecorCube 10,5.5,0,3,.15,3,38,40,44

	MakeWallCube 13,2.75,0,.15,2.75,3,70,73,78

	MakeWallCube 10,2.75,-3,3,2.75,.15,70,73,78
	MakeWallCube 10,2.75,3,3,2.75,.15,70,73,78

	MakeWallCube 7,2.75,-2,.15,2.75,1,70,73,78
	MakeWallCube 7,2.75,2,.15,2.75,1,70,73,78

	MakeSolidCube 9,1.0,-1.8,1.1,.12,.6,90,60,32
	MakeSolidCube 11.2,1.0,1.7,1.1,.12,.6,90,60,32

End Function

; ============================================================
; DOOR
; ============================================================

Function CreateDoor(index,doorZ#)

	Local left
	Local right

	left = CreateCube()

	PositionEntity left,-1.25,2.25,doorZ#
	ScaleEntity left,1.25,2.25,.12
	EntityColor left,105,72,42

	right = CreateCube()

	PositionEntity right,1.25,2.25,doorZ#
	ScaleEntity right,1.25,2.25,.12
	EntityColor right,105,72,42

	GateLeft(index) = left
	GateRight(index) = right

	GateOpen(index) = 0
	GateOffset#(index) = 0

End Function

Function IsSCPNearDoor(doorZ#)

	Local sx#
	Local sz#

	If SCP173 = 0 Then Return 0

	If SCPState = STATE_CARRY Then Return 0
	If SCPState = STATE_PICKUP Then Return 0

	sx# = EntityX(SCP173,True)
	sz# = EntityZ(SCP173,True)

	If Abs(sz#-doorZ#) < 2.2 Then

		If Abs(sx#) < 3.2 Then
			Return 1
		EndIf

	EndIf

	Return 0

End Function

Function UpdateDoor(index,doorZ#)

	Local px#
	Local pz#

	Local nearDoor

	Local leftX#
	Local rightX#

	px# = EntityX(Player)
	pz# = EntityZ(Player)

	nearDoor = 0

	If Abs(pz#-doorZ#) < 2.2 Then

		If Abs(px#) < 3.2 Then
			nearDoor = 1
		EndIf

	EndIf

	If IsSCPNearDoor(doorZ#) = 1 Then
		nearDoor = 1
	EndIf

	If nearDoor = 1 Then
		GateOpen(index) = 1
	Else
		GateOpen(index) = 0
	EndIf

	If GateOpen(index) = 1 Then

		GateOffset#(index) = GateOffset#(index)+.15

		If GateOffset#(index) > 2.0 Then
			GateOffset#(index) = 2.0
		EndIf

	Else

		GateOffset#(index) = GateOffset#(index)-.15

		If GateOffset#(index) < 0 Then
			GateOffset#(index) = 0
		EndIf

	EndIf

	leftX# = -1.25-GateOffset#(index)
	rightX# = 1.25+GateOffset#(index)

	PositionEntity GateLeft(index),leftX#,2.25,doorZ#
	PositionEntity GateRight(index),rightX#,2.25,doorZ#

End Function

Function UpdateDoors()

	UpdateDoor 0,-4
	UpdateDoor 1,4
	UpdateDoor 2,12

End Function

; ============================================================
; CRATE
; ============================================================

Function AddCrateBeam(x#,y#,z#,sx#,sy#,sz#)

	Local e

	If CrateCount >= 30 Then Return

	e = CreateCube()

	PositionEntity e,x#,y#,z#
	ScaleEntity e,sx#,sy#,sz#
	EntityColor e,128,84,40

	CrateBeams(CrateCount) = e
	CrateCount = CrateCount+1

	AddObstacle x#,z#,sx#,sz#

End Function

Function CreateCrate()

	CrateCount = 0

	CrateFloor = CreateCube()

	PositionEntity CrateFloor,-2.6,.08,.7
	ScaleEntity CrateFloor,1.5,.08,1.5
	EntityColor CrateFloor,88,56,30

	AddCrateBeam -3.95,.85,-.55,.11,.85,.11
	AddCrateBeam -1.25,.85,-.55,.11,.85,.11

	AddCrateBeam -3.95,.85,1.95,.11,.85,.11
	AddCrateBeam -1.25,.85,1.95,.11,.85,.11

	AddCrateBeam -2.6,.28,-.55,1.35,.11,.11
	AddCrateBeam -2.6,.28,1.95,1.35,.11,.11

	AddCrateBeam -3.95,.28,.7,.11,.11,1.35
	AddCrateBeam -1.25,.28,.7,.11,.11,1.35

	AddCrateBeam -2.6,.9,-.55,1.35,.10,.10
	AddCrateBeam -2.6,.9,1.95,1.35,.10,.10

	AddCrateBeam -3.95,.9,.7,.10,.10,1.35
	AddCrateBeam -1.25,.9,.7,.10,.10,1.35

	AddCrateBeam -2.6,1.65,-.55,1.35,.11,.11
	AddCrateBeam -2.6,1.65,1.95,1.35,.11,.11

	AddCrateBeam -3.95,1.65,.7,.11,.11,1.35
	AddCrateBeam -1.25,1.65,.7,.11,.11,1.35

End Function

; ============================================================
; SCP UV
; ============================================================

Function FixSCPTexture(mesh)

	Local surf
	Local verts
	Local v
	Local u#
	Local tv#
	Local s
	Local surfCount
	Local c
	Local childCount
	Local child

	If mesh = 0 Then Return

	surfCount = CountSurfaces(mesh)

	For s = 1 To surfCount

		surf = GetSurface(mesh,s)

		If surf <> 0 Then

			verts = CountVertices(surf)

			For v = 0 To verts-1

				u# = VertexU(surf,v,0)
				tv# = VertexV(surf,v,0)

				tv# = 1.0-tv#

				VertexTexCoords surf,v,u#,tv#,1.0,0

			Next

		EndIf

	Next

	childCount = CountChildren(mesh)

	For c = 1 To childCount

		child = GetChild(mesh,c)

		FixSCPTexture child

	Next

End Function

; ============================================================
; APPLY SCP
; ============================================================

Function ApplySCPTexture(texture)

	If SCP173 = 0 Then Return
	If texture = 0 Then Return

	EntityTexture SCP173,texture

	ForceSCPScale()

End Function

; ============================================================
; CREATE SCP
; ============================================================

Function CreateSCP()

	SCP173 = LoadMesh("SCP-173\source\173.3ds")

	If SCP173 = 0 Then

		RuntimeError "SCP-173 modeli bulunamadi."

	EndIf

	FixSCPTexture SCP173

	ScaleEntity SCP173,SCP_SCALE#,SCP_SCALE#,SCP_SCALE#

	RotateEntity SCP173,-90,0,0

	PositionEntity SCP173,-2.6,SCP_GROUND_Y#,.7

	SCPUnpaintedTexture = LoadTexture("SCP-173\source\173_Spec.jpg")
	SCPTexture = LoadTexture("SCP-173\source\173texture.jpg")

	If SCPAlive = 1 Then
		ApplySCPTexture SCPTexture
	Else
		ApplySCPTexture SCPUnpaintedTexture
	EndIf

	ForceSCPScale()

End Function

; ============================================================
; FORCE SCALE
; ============================================================

Function ForceSCPScale()

	If SCP173 = 0 Then Return

	ScaleEntity SCP173,SCP_SCALE#,SCP_SCALE#,SCP_SCALE#

End Function

; ============================================================
; FACE SCP
; ============================================================

Function FaceSCPToPlayer()

	Local sx#
	Local sz#

	Local px#
	Local pz#

	Local dx#
	Local dz#

	Local yaw#

	If SCP173 = 0 Then Return
	If Player = 0 Then Return

	sx# = EntityX(SCP173)
	sz# = EntityZ(SCP173)

	px# = EntityX(Player)
	pz# = EntityZ(Player)

	dx# = px#-sx#
	dz# = pz#-sz#

	If Abs(dx#) < .001 And Abs(dz#) < .001 Then Return

	yaw# = ATan2(-dx#,dz#)+180

	RotateEntity SCP173,-90,yaw#,0

End Function

; ============================================================
; MACHINE
; ============================================================

Function CreateMachine()

	Local f1
	Local f2
	Local f3
	Local f4

	Local mx#
	Local mz#

	mx# = 2.8
	mz# = .7

	MachinePlatform = CreateCube()

	PositionEntity MachinePlatform,mx#,.25,mz#
	ScaleEntity MachinePlatform,2,.25,1.5
	EntityColor MachinePlatform,45,46,50

	MachineBody = CreateCube()

	PositionEntity MachineBody,mx#,2.6,mz#
	ScaleEntity MachineBody,2.1,2.25,1.45

	EntityColor MachineBody,130,185,215
	EntityAlpha MachineBody,.12
	EntityBlend MachineBody,3

	MachineFront = CreateCube()

	PositionEntity MachineFront,mx#,2.6,mz#-1.45
	ScaleEntity MachineFront,2.1,2.25,.05

	EntityColor MachineFront,160,215,240
	EntityAlpha MachineFront,.10
	EntityBlend MachineFront,3

	MachineLeft = CreateCube()

	PositionEntity MachineLeft,mx#-2.1,2.6,mz#
	ScaleEntity MachineLeft,.05,2.25,1.45

	EntityColor MachineLeft,160,215,240
	EntityAlpha MachineLeft,.10
	EntityBlend MachineLeft,3

	MachineRight = CreateCube()

	PositionEntity MachineRight,mx#+2.1,2.6,mz#
	ScaleEntity MachineRight,.05,2.25,1.45

	EntityColor MachineRight,160,215,240
	EntityAlpha MachineRight,.10
	EntityBlend MachineRight,3

	MachineBack = CreateCube()

	PositionEntity MachineBack,mx#,2.6,mz#+1.45
	ScaleEntity MachineBack,2.1,2.25,.05

	EntityColor MachineBack,160,215,240
	EntityAlpha MachineBack,.10
	EntityBlend MachineBack,3

	f1 = MakeDecorCube(mx#-2.1,2.6,mz#,.14,2.3,1.48,50,52,58)
	f2 = MakeDecorCube(mx#+2.1,2.6,mz#,.14,2.3,1.48,50,52,58)
	f3 = MakeDecorCube(mx#,4.85,mz#,2.1,.14,1.48,50,52,58)
	f4 = MakeDecorCube(mx#,.35,mz#,2.1,.14,1.48,50,52,58)

	MachinePanel = CreateCube()

	PositionEntity MachinePanel,5.1,2.1,-.25
	ScaleEntity MachinePanel,.12,1.1,.75
	EntityColor MachinePanel,27,29,34

	AddObstacle 5.1,-.25,.12,.75

	MachineScreen = CreateCube()

	PositionEntity MachineScreen,4.95,2.75,-.25
	ScaleEntity MachineScreen,.035,.35,.45
	EntityColor MachineScreen,30,150,200

	MachineButton = CreateSphere(16)

	PositionEntity MachineButton,4.92,1.45,-.25
	ScaleEntity MachineButton,.22,.22,.22
	EntityColor MachineButton,190,35,35

	MachineLight = CreateLight(2)

	PositionEntity MachineLight,mx#,4.4,mz#
	LightRange MachineLight,7
	LightColor MachineLight,55,130,210

	AlarmLight = CreateLight(2)

	PositionEntity AlarmLight,mx#,4.4,mz#
	LightRange AlarmLight,6
	LightColor AlarmLight,220,35,35

	HideEntity AlarmLight

End Function

; ============================================================
; MACHINE DISTANCE
; ============================================================

Function IsNearMachine()

	Local dx#
	Local dz#
	Local dist#

	dx# = EntityX(Player)-5.1
	dz# = EntityZ(Player)-(-.25)

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# < 2.8 Then Return 1

	Return 0

End Function

; ============================================================
; CRATE DISTANCE
; ============================================================

Function IsNearCrate()

	Local dx#
	Local dz#
	Local dist#

	dx# = EntityX(Player)-(-2.6)
	dz# = EntityZ(Player)-.7

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# < 2.6 Then Return 1

	Return 0

End Function

; ============================================================
; PLAYER
; ============================================================

Function CreatePlayer()

	Player = CreatePivot()

	PositionEntity Player,0,PLAYER_HEIGHT,-8

	EntityRadius Player,PLAYER_RADIUS#

	Camera = CreateCamera(Player)

	PositionEntity Camera,0,.65,0

	CameraRange Camera,.05,180

	CameraClsColor Camera,32,38,50

	Flashlight = CreateLight(3)

	EntityParent Flashlight,Camera

	PositionEntity Flashlight,0,0,0
	RotateEntity Flashlight,0,0,0

	LightRange Flashlight,15
	LightColor Flashlight,255,244,214

	LightConeAngles Flashlight,26,40

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

End Function

; ============================================================
; ATMOSPHERE
; ============================================================

Function SetupAtmosphere()

	AmbientLight 46,48,56

End Function

; ============================================================
; MUSIC
; ============================================================

Function UpdateMusicLoop()

	If ChannelPlaying(MusicChannel) = 1 Then Return

	If CurrentMusic = 0 Then

		MusicChannel = PlaySound(OstSound)

	Else

		MusicChannel = PlaySound(ThemeSound)

	EndIf

End Function

; ============================================================
; CREEPY LIGHT
; ============================================================

Function UpdateCreepyLight()

	Local flicker#

	If CreepyLight = 0 Then Return

	If Rnd(0,100) < 2 Then

		flicker# = Rnd(35,150)

		LightColor CreepyLight,flicker#,flicker#*1.02,flicker#*1.06

	Else

		LightColor CreepyLight,150,156,168

	EndIf

End Function

; ============================================================
; PLAYER X
; ============================================================

Function TryMovePlayerX(amount#)

	Local oldX#
	Local oldY#
	Local oldZ#

	oldX# = EntityX(Player)
	oldY# = EntityY(Player)
	oldZ# = EntityZ(Player)

	PositionEntity Player,oldX#+amount#,oldY#,oldZ#

	If WorldBlocked(EntityX(Player),EntityZ(Player),PLAYER_RADIUS#) = 1 Then

		PositionEntity Player,oldX#,oldY#,oldZ#

	Else

		If IsSCPBlockingPlayer(EntityX(Player),EntityZ(Player),PLAYER_RADIUS#) = 1 Then

			PositionEntity Player,oldX#,oldY#,oldZ#

		EndIf

	EndIf

End Function

; ============================================================
; PLAYER Z
; ============================================================

Function TryMovePlayerZ(amount#)

	Local oldX#
	Local oldY#
	Local oldZ#

	oldX# = EntityX(Player)
	oldY# = EntityY(Player)
	oldZ# = EntityZ(Player)

	PositionEntity Player,oldX#,oldY#,oldZ#+amount#

	If WorldBlocked(EntityX(Player),EntityZ(Player),PLAYER_RADIUS#) = 1 Then

		PositionEntity Player,oldX#,oldY#,oldZ#

	Else

		If IsSCPBlockingPlayer(EntityX(Player),EntityZ(Player),PLAYER_RADIUS#) = 1 Then

			PositionEntity Player,oldX#,oldY#,oldZ#

		EndIf

	EndIf

End Function

; ============================================================
; PLAYER MOVEMENT
; ============================================================

Function UpdatePlayer(dt#)

	Local speed#

	Local forward
	Local backward
	Local left
	Local right

	Local forwardX#
	Local forwardZ#

	Local rightX#
	Local rightZ#

	Local moveX#
	Local moveZ#

	Local moveLength#

	If PlayerDead = 1 Then Return

	speed# = PLAYER_WALK_SPEED#

	If KeyDown(42) Then speed# = PLAYER_RUN_SPEED#
	If KeyDown(54) Then speed# = PLAYER_RUN_SPEED#

	forward = 0
	backward = 0
	left = 0
	right = 0

	If KeyDown(17) Then forward = 1
	If KeyDown(31) Then backward = 1
	If KeyDown(30) Then left = 1
	If KeyDown(32) Then right = 1

	forwardX# = -Sin(CamYaw#)
	forwardZ# = Cos(CamYaw#)

	rightX# = Cos(CamYaw#)
	rightZ# = Sin(CamYaw#)

	moveX# = 0
	moveZ# = 0

	If forward = 1 Then
		moveX# = moveX#+forwardX#
		moveZ# = moveZ#+forwardZ#
	EndIf

	If backward = 1 Then
		moveX# = moveX#-forwardX#
		moveZ# = moveZ#-forwardZ#
	EndIf

	If right = 1 Then
		moveX# = moveX#+rightX#
		moveZ# = moveZ#+rightZ#
	EndIf

	If left = 1 Then
		moveX# = moveX#-rightX#
		moveZ# = moveZ#-rightZ#
	EndIf

	If moveX# <> 0 Or moveZ# <> 0 Then

		moveLength# = Sqr(moveX#*moveX#+moveZ#*moveZ#)

		If moveLength# > 0 Then

			moveX# = moveX#/moveLength#
			moveZ# = moveZ#/moveLength#

		EndIf

		moveX# = moveX#*speed#*dt#
		moveZ# = moveZ#*speed#*dt#

		TryMovePlayerX moveX#
		TryMovePlayerZ moveZ#

		If speed# = PLAYER_RUN_SPEED# Then
			UpdateFootsteps(2)
		Else
			UpdateFootsteps(1)
		EndIf

	Else

		UpdateFootsteps(0)

	EndIf

End Function

; ============================================================
; FOOTSTEPS
; ============================================================

Function UpdateFootsteps(mode)

	If PlayerDead = 1 Then mode = 0

	If mode = FootstepMode Then

		If mode <> 0 Then

			If ChannelPlaying(FootstepChannel) = 0 Then

				If mode = 1 Then
					FootstepChannel = PlaySound(WalkSound)
				Else
					FootstepChannel = PlaySound(RunSound)
				EndIf

			EndIf

		EndIf

		Return

	EndIf

	If FootstepMode <> 0 Then StopChannel FootstepChannel

	If mode = 1 Then
		FootstepChannel = PlaySound(WalkSound)
	EndIf

	If mode = 2 Then
		FootstepChannel = PlaySound(RunSound)
	EndIf

	FootstepMode = mode

End Function

; ============================================================
; MOUSE
; ============================================================

Function UpdateMouse()

	Local mx
	Local my

	mx = MouseX()-GraphicsWidth()/2
	my = MouseY()-GraphicsHeight()/2

	CamYaw# = CamYaw#-Float(mx)*MOUSE_SENS
	CamPitch# = CamPitch#+Float(my)*MOUSE_SENS

	If CamPitch# > 88 Then CamPitch# = 88
	If CamPitch# < -88 Then CamPitch# = -88

	RotateEntity Player,0,CamYaw#,0
	RotateEntity Camera,CamPitch#,0,0

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

End Function

; ============================================================
; BLINK
; ============================================================

Function StartBlink()

	If BlinkActive = 1 Then Return
	If PlayerDead = 1 Then Return

	BlinkActive = 1

	BlinkStartTime = MilliSecs()

	BlinkSCPStartX# = EntityX(SCP173)
	BlinkSCPStartZ# = EntityZ(SCP173)

	BlinkSCPDistance# = 0

End Function

Function UpdateBlink()

	Local now

	If BlinkActive = 0 Then Return

	now = MilliSecs()

	If now >= BlinkStartTime+BLINK_DURATION Then

		BlinkActive = 0
		BlinkStartTime = 0

	EndIf

End Function

; ============================================================
; LOOK SCP
; ============================================================

Function IsLookingAtSCP()

	Local px#
	Local pz#

	Local sx#
	Local sz#

	Local dx#
	Local dz#

	Local dist#

	Local viewX#
	Local viewZ#

	Local targetX#
	Local targetZ#

	Local dot#
	Local angle#

	If SCP173 = 0 Then Return 0

	px# = EntityX(Camera,True)
	pz# = EntityZ(Camera,True)

	sx# = EntityX(SCP173,True)
	sz# = EntityZ(SCP173,True)

	dx# = sx#-px#
	dz# = sz#-pz#

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# < .01 Then Return 1

	viewX# = -Sin(CamYaw#)
	viewZ# = Cos(CamYaw#)

	targetX# = dx#/dist#
	targetZ# = dz#/dist#

	dot# = viewX#*targetX#+viewZ#*targetZ#

	If dot# > 1 Then dot# = 1
	If dot# < -1 Then dot# = -1

	angle# = ACos(dot#)

	If angle# < 45 Then Return 1

	Return 0

End Function

; ============================================================
; SCP MOVE FACTOR
; ============================================================

Function GetSCPMoveFactor#()

	Local px#
	Local pz#

	Local sx#
	Local sz#

	Local dx#
	Local dz#

	Local dist#

	Local viewX#
	Local viewZ#

	Local targetX#
	Local targetZ#

	Local dot#
	Local angle#
	Local factor#

	px# = EntityX(Camera,True)
	pz# = EntityZ(Camera,True)

	sx# = EntityX(SCP173,True)
	sz# = EntityZ(SCP173,True)

	dx# = sx#-px#
	dz# = sz#-pz#

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# < .01 Then Return 0

	viewX# = -Sin(CamYaw#)
	viewZ# = Cos(CamYaw#)

	targetX# = dx#/dist#
	targetZ# = dz#/dist#

	dot# = viewX#*targetX#+viewZ#*targetZ#

	If dot# > 1 Then dot# = 1
	If dot# < -1 Then dot# = -1

	angle# = ACos(dot#)

	If angle# <= 45 Then Return 0

	factor# = (angle#-45.0)/135.0

	If factor# < 0 Then factor# = 0
	If factor# > .82 Then factor# = .82

	Return factor#

End Function

; ============================================================
; DIRECT SCP MOVEMENT
; ============================================================

Function MoveSCPDirect(dt#)

	Local sx#
	Local sz#

	Local px#
	Local pz#

	Local dx#
	Local dz#

	Local dist#

	Local moveFactor#
	Local moveAmount#
	Local speed#

	Local wantedX#
	Local wantedZ#

	Local oldX#
	Local oldZ#

	Local movedX#
	Local movedZ#

	Local remainingDistance#

	If SCP173 = 0 Then Return

	sx# = EntityX(SCP173)
	sz# = EntityZ(SCP173)

	px# = EntityX(Player)
	pz# = EntityZ(Player)

	dx# = px#-sx#
	dz# = pz#-sz#

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# <= .01 Then

		FaceSCPToPlayer()

		Return

	EndIf

	If BlinkActive = 1 Then

		moveFactor# = 1.0

	Else

		moveFactor# = GetSCPMoveFactor#()

		If moveFactor# <= 0 Then

			FaceSCPToPlayer()

			Return

		EndIf

	EndIf

	speed# = SCP_BLINK_SPEED#

	moveAmount# = speed#*moveFactor#*dt#

	If moveAmount# > dist# Then
		moveAmount# = dist#
	EndIf

	If BlinkActive = 1 Then

		remainingDistance# = SCP_BLINK_MAX_DISTANCE#-BlinkSCPDistance#

		If remainingDistance# <= 0 Then

			FaceSCPToPlayer()

			Return

		EndIf

		If moveAmount# > remainingDistance# Then
			moveAmount# = remainingDistance#
		EndIf

	EndIf

	wantedX# = dx#/dist#*moveAmount#
	wantedZ# = dz#/dist#*moveAmount#

	; X

	oldX# = EntityX(SCP173)
	oldZ# = EntityZ(SCP173)

	PositionEntity SCP173,oldX#+wantedX#,SCP_GROUND_Y#,oldZ#

	If WorldBlocked(EntityX(SCP173),EntityZ(SCP173),SCP_RADIUS#+.08) = 1 Then

		PositionEntity SCP173,oldX#,SCP_GROUND_Y#,oldZ#

	Else

		movedX# = Abs(EntityX(SCP173)-oldX#)

		If BlinkActive = 1 Then
			BlinkSCPDistance# = BlinkSCPDistance#+movedX#
		EndIf

	EndIf

	; Z

	oldX# = EntityX(SCP173)
	oldZ# = EntityZ(SCP173)

	PositionEntity SCP173,oldX#,SCP_GROUND_Y#,oldZ#+wantedZ#

	If WorldBlocked(EntityX(SCP173),EntityZ(SCP173),SCP_RADIUS#+.08) = 1 Then

		PositionEntity SCP173,oldX#,SCP_GROUND_Y#,oldZ#

	Else

		movedZ# = Abs(EntityZ(SCP173)-oldZ#)

		If BlinkActive = 1 Then
			BlinkSCPDistance# = BlinkSCPDistance#+movedZ#
		EndIf

	EndIf

	If BlinkActive = 1 Then

		If BlinkSCPDistance# > SCP_BLINK_MAX_DISTANCE# Then

			PositionEntity SCP173,BlinkSCPStartX#,SCP_GROUND_Y#,BlinkSCPStartZ#

			PositionEntity SCP173,BlinkSCPStartX#+dx#/dist#*SCP_BLINK_MAX_DISTANCE#,SCP_GROUND_Y#,BlinkSCPStartZ#+dz#/dist#*SCP_BLINK_MAX_DISTANCE#

			BlinkSCPDistance# = SCP_BLINK_MAX_DISTANCE#

		EndIf

	EndIf

	FaceSCPToPlayer()

	ForceSCPScale()

End Function

; ============================================================
; UPDATE SCP
; ============================================================

Function UpdateSCP(dt#)

	If SCP173 = 0 Then Return

	ForceSCPScale()

	If SCPState <> STATE_ACTIVE And SCPState <> STATE_ATTACK Then Return

	If SCPState = STATE_ATTACK Then Return

	If BlinkActive = 0 Then

		If IsLookingAtSCP() = 1 Then Return

	EndIf

	MoveSCPDirect dt#

	ForceSCPScale()

End Function

; ============================================================
; ATTACK
; ============================================================

Function CheckSCPAttack()

	Local px#
	Local pz#

	Local sx#
	Local sz#

	Local dx#
	Local dz#

	Local dist#
	Local looking

	If SCP173 = 0 Then Return
	If PlayerDead = 1 Then Return
	If SCPState <> STATE_ACTIVE Then Return

	If BlinkActive = 0 Then

		looking = IsLookingAtSCP()

		If looking = 1 Then Return

	EndIf

	px# = EntityX(Player)
	pz# = EntityZ(Player)

	sx# = EntityX(SCP173)
	sz# = EntityZ(SCP173)

	dx# = px#-sx#
	dz# = pz#-sz#

	dist# = Sqr(dx#*dx#+dz#*dz#)

	If dist# <= SCP_ATTACK_DISTANCE# Then

		PlayerDead = 1
		DeathTime = MilliSecs()

		SCPState = STATE_ATTACK

		FaceSCPToPlayer()

	EndIf

End Function

; ============================================================
; GRID
; ============================================================

Function WorldToGridX(x#)

	Local gx

	gx = Int((x#-NAV_MIN_X#)/NAV_CELL#+.5)

	If gx < 0 Then gx = 0
	If gx >= NAV_W Then gx = NAV_W-1

	Return gx

End Function

Function WorldToGridZ(z#)

	Local gz

	gz = Int((z#-NAV_MIN_Z#)/NAV_CELL#+.5)

	If gz < 0 Then gz = 0
	If gz >= NAV_H Then gz = NAV_H-1

	Return gz

End Function

Function GridToWorldX#(gx)

	Return NAV_MIN_X#+Float(gx)*NAV_CELL#

End Function

Function GridToWorldZ#(gz)

	Return NAV_MIN_Z#+Float(gz)*NAV_CELL#

End Function

Function NodeIndex(gx,gz)

	Return gx+gz*NAV_W

End Function

; ============================================================
; NAV BLOCK
; ============================================================

Function NavBlocked(gx,gz)

	Local x#
	Local z#

	x# = GridToWorldX#(gx)
	z# = GridToWorldZ#(gz)

	If WorldBlocked(x#,z#,SCP_RADIUS#+.08) = 1 Then Return 1

	Return 0

End Function

; ============================================================
; HEURISTIC
; ============================================================

Function NavHeuristic#(gx1,gz1,gx2,gz2)

	Local dx#
	Local dz#

	dx# = Abs(Float(gx2-gx1))
	dz# = Abs(Float(gz2-gz1))

	If dx# > dz# Then

		Return dx#+(1.4142-dx#)*dz#

	EndIf

	Return dz#+(1.4142-dz#)*dx#

End Function

; ============================================================
; RESET ASTAR
; ============================================================

Function ResetAStar()

	Local i

	For i = 0 To NAV_NODES-1

		NavOpen(i) = 0
		NavClosed(i) = 0
		NavParent(i) = -1

		NavG#(i) = 999999
		NavH#(i) = 0
		NavF#(i) = 999999

	Next

End Function

; ============================================================
; LOWEST OPEN
; ============================================================

Function LowestOpenNode()

	Local i
	Local best
	Local bestF#

	best = -1
	bestF# = 999999999

	For i = 0 To NAV_NODES-1

		If NavOpen(i) = 1 Then

			If NavClosed(i) = 0 Then

				If NavF#(i) < bestF# Then

					bestF# = NavF#(i)
					best = i

				EndIf

			EndIf

		EndIf

	Next

	Return best

End Function

; ============================================================
; FIND PATH
; ============================================================

Function FindSCPPath(startX#,startZ#,goalX#,goalZ#)

	Local startGX
	Local startGZ

	Local goalGX
	Local goalGZ

	Local startNode
	Local goalNode

	Local current

	Local cgx
	Local cgz

	Local nx
	Local nz

	Local neighbor

	Local tentative#
	Local moveCost#

	Local dx
	Local dz

	Local i

	Local pathNode
	Local pathCount

	Local tempX#
	Local tempZ#

	Local safety

	Local searchRadius
	Local foundGoal

	Local cornerBlocked

	ResetAStar()

	startGX = WorldToGridX(startX#)
	startGZ = WorldToGridZ(startZ#)

	goalGX = WorldToGridX(goalX#)
	goalGZ = WorldToGridZ(goalZ#)

	If NavBlocked(startGX,startGZ) = 1 Then Return 0

	If NavBlocked(goalGX,goalGZ) = 1 Then

		foundGoal = 0

		For searchRadius = 1 To 4

			For dz = -searchRadius To searchRadius

				For dx = -searchRadius To searchRadius

					nx = goalGX+dx
					nz = goalGZ+dz

					If nx >= 0 And nx < NAV_W Then

						If nz >= 0 And nz < NAV_H Then

							If NavBlocked(nx,nz) = 0 Then

								goalGX = nx
								goalGZ = nz

								foundGoal = 1

							EndIf

						EndIf

					EndIf

					If foundGoal = 1 Then Exit

				Next

				If foundGoal = 1 Then Exit

			Next

			If foundGoal = 1 Then Exit

		Next

		If foundGoal = 0 Then Return 0

	EndIf

	startNode = NodeIndex(startGX,startGZ)
	goalNode = NodeIndex(goalGX,goalGZ)

	NavG#(startNode) = 0
	NavH#(startNode) = NavHeuristic#(startGX,startGZ,goalGX,goalGZ)
	NavF#(startNode) = NavH#(startNode)

	NavOpen(startNode) = 1

	safety = 0

	While safety < NAV_NODES

		safety = safety+1

		current = LowestOpenNode()

		If current = -1 Then Return 0

		If current = goalNode Then Exit

		NavOpen(current) = 0
		NavClosed(current) = 1

		cgx = current Mod NAV_W
		cgz = Int(current/NAV_W)

		For dz = -1 To 1

			For dx = -1 To 1

				If dx <> 0 Or dz <> 0 Then

					nx = cgx+dx
					nz = cgz+dz

					If nx >= 0 And nx < NAV_W Then

						If nz >= 0 And nz < NAV_H Then

							neighbor = NodeIndex(nx,nz)

							If NavClosed(neighbor) = 0 Then

								If NavBlocked(nx,nz) = 0 Then

									cornerBlocked = 0

									If dx <> 0 And dz <> 0 Then

										If NavBlocked(cgx+dx,cgz) = 1 Then
											cornerBlocked = 1
										EndIf

										If NavBlocked(cgx,cgz+dz) = 1 Then
											cornerBlocked = 1
										EndIf

									EndIf

									If cornerBlocked = 0 Then

										moveCost# = 1.0

										If dx <> 0 And dz <> 0 Then
											moveCost# = 1.4142
										EndIf

										tentative# = NavG#(current)+moveCost#

										If NavOpen(neighbor) = 0 Then

											NavOpen(neighbor) = 1
											NavParent(neighbor) = current
											NavG#(neighbor) = tentative#
											NavH#(neighbor) = NavHeuristic#(nx,nz,goalGX,goalGZ)
											NavF#(neighbor) = NavG#(neighbor)+NavH#(neighbor)

										Else

											If tentative# < NavG#(neighbor) Then

												NavParent(neighbor) = current
												NavG#(neighbor) = tentative#
												NavF#(neighbor) = NavG#(neighbor)+NavH#(neighbor)

											EndIf

										EndIf

									EndIf

								EndIf

							EndIf

						EndIf

					EndIf

				EndIf

			Next

		Next

	Wend

	If current <> goalNode Then Return 0

	pathCount = 0
	pathNode = goalNode

	While pathNode <> -1 And pathCount < 2047

		cgx = pathNode Mod NAV_W
		cgz = Int(pathNode/NAV_W)

		PathX#(pathCount) = GridToWorldX#(cgx)
		PathZ#(pathCount) = GridToWorldZ#(cgz)

		pathCount = pathCount+1

		If pathNode = startNode Then Exit

		pathNode = NavParent(pathNode)

	Wend

	If pathCount <= 0 Then Return 0

	For i = 0 To Int(pathCount/2)-1

		tempX# = PathX#(i)
		tempZ# = PathZ#(i)

		PathX#(i) = PathX#(pathCount-1-i)
		PathZ#(i) = PathZ#(pathCount-1-i)

		PathX#(pathCount-1-i) = tempX#
		PathZ#(pathCount-1-i) = tempZ#

	Next

	SCPPathCount = pathCount
	SCPPathIndex = 0

	Return 1

End Function

; ============================================================
; PICKUP
; ============================================================

Function TryPickup()

	If EPressed = 0 Then Return
	If SCPState <> STATE_NORMAL Then Return
	If IsNearCrate() = 0 Then Return

	SCPState = STATE_PICKUP
	SCPAnimTimer = 0

End Function

Function UpdatePickup()

	If SCPState <> STATE_PICKUP Then Return

	SCPAnimTimer = SCPAnimTimer+1

	If SCPAnimTimer < 20 Then

		PositionEntity SCP173,-2.6,.45,.7
		RotateEntity SCP173,-90,0,0

		ForceSCPScale()

	Else

		SCPState = STATE_CARRY
		SCPPicked = 1

		EntityParent SCP173,Player

		PositionEntity SCP173,0,.15,-1.1
		RotateEntity SCP173,0,0,0

		ForceSCPScale()

	EndIf

End Function

; ============================================================
; CARRY
; ============================================================

Function UpdateCarry()

	If SCPState <> STATE_CARRY Then Return

	ForceSCPScale()

	If EPressed = 1 Then

		If IsNearMachine() = 1 Then

			EntityParent SCP173,0

			PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
			RotateEntity SCP173,-90,0,0

			ForceSCPScale()

			SCPState = STATE_PLACE
			SCPAnimTimer = 0

		EndIf

	EndIf

End Function

; ============================================================
; PLACE
; ============================================================

Function UpdatePlace()

	If SCPState <> STATE_PLACE Then Return

	SCPAnimTimer = SCPAnimTimer+1

	If SCPAnimTimer < 20 Then

		PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
		RotateEntity SCP173,-90,0,0

		ForceSCPScale()

	Else

		PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
		RotateEntity SCP173,-90,0,0

		ForceSCPScale()

		SCPPlaced = 1
		SCPState = STATE_MACHINE_READY

		MachineReady = 1
		MachineRunning = 0

		SCPAnimTimer = 0

	EndIf

End Function

; ============================================================
; MACHINE START
; ============================================================

Function TryMachine()

	If EPressed = 0 Then Return

	If MachineReady = 0 Then Return
	If MachineRunning = 1 Then Return

	If IsNearMachine() = 0 Then Return

	SCPState = STATE_ACTIVATE

	MachineRunning = 1
	MachineAnimTimer = 0

	StopChannel MusicChannel

	MusicChannel = PlaySound(ThemeSound)

	CurrentMusic = 1

	ShowEntity AlarmLight

End Function

; ============================================================
; MACHINE UPDATE
; ============================================================

Function UpdateMachine()

	If SCPState <> STATE_ACTIVATE Then Return

	MachineAnimTimer = MachineAnimTimer+1

	If (MachineAnimTimer Mod 8) < 4 Then
		EntityColor MachineScreen,40,220,255
	Else
		EntityColor MachineScreen,10,50,70
	EndIf

	If (MachineAnimTimer Mod 10) < 5 Then
		LightColor AlarmLight,255,40,40
	Else
		LightColor AlarmLight,70,10,10
	EndIf

	If MachineAnimTimer >= 75 Then

		HideEntity AlarmLight

		EntityColor MachineScreen,30,220,80

		SCPAlive = 1
		SCPState = STATE_ACTIVE

		ApplySCPTexture SCPTexture

		PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
		RotateEntity SCP173,-90,0,0

		ForceSCPScale()

		SCPPathCount = 0
		SCPPathIndex = 0
		SCPPathTimer = 0

		BlinkSCPStartX# = EntityX(SCP173)
		BlinkSCPStartZ# = EntityZ(SCP173)

		BlinkSCPDistance# = 0

	EndIf

End Function

; ============================================================
; OUTDOOR
; ============================================================

Function CreateOutdoor()

	Local grass

	grass = MakeGrassGround()

	MakeDecorCube 0,.02,26,2.2,.015,14,82,70,52
	MakeDecorCube 0,.02,46,2.2,.015,8,82,70,52

	CreateTree -12,27
	CreateTree 13,30

	CreateTree -15,45
	CreateTree 15,48

	CreateTree -10,66
	CreateTree 10,68

	CreateTree -21,55
	CreateTree 21,60

	CreateRock -8,36,1.2
	CreateRock 8,38,1.0

	CreateRock -11,54,1.5
	CreateRock 13,57,1.4

	CreateRock -18,70,1.2
	CreateRock 18,72,1.4

	CreateOutdoorLamp -5,24
	CreateOutdoorLamp 5,24

	CreateOutdoorLamp -5,42
	CreateOutdoorLamp 5,42

	CreateOutdoorLamp -5,60
	CreateOutdoorLamp 5,60

	CreateFence

	CreateCloud -25,30,14
	CreateCloud 25,48,18
	CreateCloud -20,70,23

	CreateOutdoorDetails()

End Function

; ============================================================
; OUTDOOR DETAILS
; ============================================================

Function CreateOutdoorDetails()

	Local e

	e = CreateSphere(16)

	PositionEntity e,-18,42,140
	ScaleEntity e,6,6,6
	EntityColor e,235,235,215

	e = CreateCube()

	PositionEntity e,0,3,150
	ScaleEntity e,90,6,2
	EntityColor e,20,26,24

	e = CreateCube()

	PositionEntity e,-40,2,130
	ScaleEntity e,50,4,2
	EntityColor e,24,30,28

	CreateStar -30,55,60
	CreateStar 22,60,80
	CreateStar -8,65,110
	CreateStar 30,58,50
	CreateStar -22,50,95
	CreateStar 12,62,130

	CreateBush -6,32
	CreateBush 6,34
	CreateBush -17,50
	CreateBush 17,52
	CreateBush -9,64
	CreateBush 9,70
	CreateBush -23,58

	MakeDecorCube -12.6,.08,27.6,.12,.08,.12,180,60,60
	MakeDecorCube -12.3,.06,27.5,.09,.06,.09,200,90,90
	MakeDecorCube 13.4,.08,30.5,.12,.08,.12,170,55,55

	CreateRock -4,20,.35
	CreateRock 4,22,.30
	CreateRock -16,80,.9
	CreateRock 16,85,1.0

	CreateTree -25,80
	CreateTree 25,85
	CreateTree -6,95
	CreateTree 6,92

	CreateBridge 50

	CreateHouse -20,38
	CreateHouse 22,58
	CreateHouse -19,78

End Function

; ============================================================
; BRIDGE
; ============================================================

Function CreateBridge(bridgeZ#)

	Local e
	Local i

	e = CreateCube()

	PositionEntity e,0,-.02,bridgeZ#
	ScaleEntity e,28,.02,3.2
	EntityColor e,28,58,66

	e = CreateCube()

	PositionEntity e,0,.02,bridgeZ#
	ScaleEntity e,3.4,.05,4.2
	EntityColor e,120,84,48

	For i = -3 To 3

		e = CreateCube()

		PositionEntity e,0,.06,bridgeZ#+Float(i)*.6
		ScaleEntity e,3.4,.02,.08
		EntityColor e,96,66,36

	Next

	For i = -3 To 3

		MakeDecorCube -3.3,.55,bridgeZ#+Float(i)*.6,.06,.55,.06,80,56,30
		MakeDecorCube 3.3,.55,bridgeZ#+Float(i)*.6,.06,.55,.06,80,56,30

	Next

	MakeDecorCube -3.3,1.05,bridgeZ#,.06,.06,4.2,86,60,32
	MakeDecorCube 3.3,1.05,bridgeZ#,.06,.06,4.2,86,60,32

End Function

; ============================================================
; HOUSE
; ============================================================

Function CreateHouse(x#,z#)

	Local e

	MakeWallCube x#,1.1,z#-1.6,2.0,1.1,.12,150,128,96
	MakeWallCube x#,1.1,z#+1.6,2.0,1.1,.12,150,128,96
	MakeWallCube x#-2.0,1.1,z#,.12,1.1,1.6,150,128,96
	MakeWallCube x#+2.0,1.1,z#,.12,1.1,1.6,150,128,96

	e = CreateCube()

	PositionEntity e,x#,2.35,z#
	ScaleEntity e,2.3,.1,1.9
	RotateEntity e,18,0,0
	EntityColor e,74,42,34

	e = CreateCube()

	PositionEntity e,x#,2.55,z#
	ScaleEntity e,2.3,.1,1.9
	RotateEntity e,-18,0,0
	EntityColor e,74,42,34

	MakeDecorCube x#,.75,z#+1.66,.55,.75,.05,60,40,26

	MakeDecorCube x#-1.1,1.3,z#+1.66,.35,.35,.04,120,170,190
	MakeDecorCube x#+1.1,1.3,z#+1.66,.35,.35,.04,120,170,190

	MakeDecorCube x#+1.2,2.9,z#-.4,.22,.5,.22,90,88,84

End Function

; ============================================================
; STAR
; ============================================================

Function CreateStar(x#,y#,z#)

	Local e

	e = CreateSphere(6)

	PositionEntity e,x#,y#,z#
	ScaleEntity e,.25,.25,.25
	EntityColor e,250,250,240

End Function

; ============================================================
; BUSH
; ============================================================

Function CreateBush(x#,z#)

	Local e

	e = CreateSphere(8)

	PositionEntity e,x#,.35,z#
	ScaleEntity e,.55,.4,.55
	EntityColor e,52,86,40

	e = CreateSphere(8)

	PositionEntity e,x#+.3,.28,z#+.2
	ScaleEntity e,.4,.32,.4
	EntityColor e,58,94,44

End Function

; ============================================================
; TREE
; ============================================================

Function CreateTree(x#,z#)

	Local trunk
	Local top

	trunk = CreateCylinder(10)

	PositionEntity trunk,x#,1.2,z#
	ScaleEntity trunk,.32,1.2,.32
	EntityColor trunk,88,58,30

	AddObstacle x#,z#,.38,.38

	top = CreateSphere(12)

	PositionEntity top,x#,3.0,z#
	ScaleEntity top,1.5,1.6,1.5
	EntityColor top,42,92,36

End Function

; ============================================================
; ROCK
; ============================================================

Function CreateRock(x#,z#,size#)

	Local rock

	rock = CreateSphere(10)

	PositionEntity rock,x#,size#*.4,z#
	ScaleEntity rock,size#,size#*.45,size#

	EntityColor rock,82,84,79

	AddObstacle x#,z#,size#,size#

End Function

; ============================================================
; OUTDOOR LIGHT
; ============================================================

Function CreateOutdoorLamp(x#,z#)

	Local pole
	Local lamp
	Local light

	pole = CreateCylinder(8)

	PositionEntity pole,x#,1.7,z#
	ScaleEntity pole,.10,1.7,.10
	EntityColor pole,40,40,42

	AddObstacle x#,z#,.14,.14

	lamp = CreateSphere(12)

	PositionEntity lamp,x#,3.45,z#
	ScaleEntity lamp,.22,.22,.22
	EntityColor lamp,225,220,175

	light = CreateLight(2)

	PositionEntity light,x#,3.3,z#
	LightRange light,6
	LightColor light,165,150,110

End Function

; ============================================================
; FENCE
; ============================================================

Function CreateFence()

	CreateFenceLine -28,15,-28,90
	CreateFenceLine 28,15,28,90
	CreateFenceLine -28,90,28,90

End Function

Function CreateFenceLine(x1#,z1#,x2#,z2#)

	Local i
	Local count

	Local dx#
	Local dz#

	Local px#
	Local pz#

	dx# = x2#-x1#
	dz# = z2#-z1#

	count = Int(Sqr(dx#*dx#+dz#*dz#)/4)

	If count < 1 Then count = 1

	For i = 0 To count

		px# = x1#+dx#*(Float(i)/Float(count))
		pz# = z1#+dz#*(Float(i)/Float(count))

		MakeDecorCube px#,1,pz#,.08,1,.08,48,52,52

	Next

End Function

; ============================================================
; CLOUD
; ============================================================

Function CreateCloud(x#,y#,z#)

	Local cloud

	cloud = CreateSphere(12)

	PositionEntity cloud,x#,y#,z#
	ScaleEntity cloud,4,.8,2

	EntityColor cloud,245,245,245

End Function

; ============================================================
; ROOM DETAILS
; ============================================================

Function CreateRoomDetails()

	Local i
	Local e

	MakeSolidCube -3.5,1,-8.6,1.5,.12,.7,70,50,30

	MakeSolidCube -4.7,.5,-8.6,.10,.5,.10,48,48,48
	MakeSolidCube -2.3,.5,-8.6,.10,.5,.10,48,48,48

	MakeSolidCube 3.8,.5,-9.6,.8,.5,.8,95,65,34

	For i = -2 To 2

		e = CreateCylinder(8)

		PositionEntity e,-5.4,3.7,Float(i)-8.5

		ScaleEntity e,.11,.11,1.5

		RotateEntity e,0,90,0

		EntityColor e,105,110,115

	Next

	MakeSolidCube -3.6,1,7.4,1.7,.12,.7,55,58,65

	MakeSolidCube -4.8,.5,7.4,.10,.5,.10,45,45,45
	MakeSolidCube -2.4,.5,7.4,.10,.5,.10,45,45,45

	e = CreateCube()

	PositionEntity e,-3.6,1.65,7.4
	ScaleEntity e,1.1,.55,.08
	EntityColor e,20,80,100

	CreateIndoorClutter()

End Function

; ============================================================
; INDOOR CLUTTER
; ============================================================

Function CreateIndoorClutter()

	Local i
	Local e

	For i = -10 To 10 Step 5

		e = CreateCylinder(8)

		PositionEntity e,6.6,5.1,Float(i)
		ScaleEntity e,.08,.08,2.4

		RotateEntity e,0,0,90

		EntityColor e,90,94,98

	Next

	MakeDecorCube -6.85,3.4,-6,.05,.5,.7,60,62,66
	MakeDecorCube -6.85,3.4,6,.05,.5,.7,60,62,66

	MakeDecorCube 0,.55,-4,2.6,.06,.03,220,190,20
	MakeDecorCube 0,.55,4,2.6,.06,.03,220,190,20

	MakeDecorCube -6.8,5.2,-11.6,.35,.02,.35,150,150,150
	MakeDecorCube 6.8,5.2,11.6,.35,.02,.35,150,150,150

	MakeDecorCube -1.4,.005,-2.5,.9,.005,.6,30,30,28
	MakeDecorCube 2.1,.005,9.3,.7,.005,.5,28,26,24

	e = CreateCube()

	PositionEntity e,0,4.6,11.85
	ScaleEntity e,.6,.25,.05
	EntityColor e,200,30,30

	MakeDecorCube 12.4,.35,-2.2,.35,.35,.3,120,90,55
	MakeDecorCube 12.4,.9,-2.2,.3,.28,.25,110,80,48
	MakeDecorCube 9.4,.9,1.6,.5,.05,.9,80,55,32

	MakeDecorCube -6.9,3.6,-1,.03,1.4,2.0,52,54,58
	MakeDecorCube 6.9,3.6,9,.03,1.4,2.0,52,54,58

End Function

; ============================================================
; HUD
; ============================================================

Function DrawHUD()

	Local cx
	Local cy
	Local pulse#

	cx = GraphicsWidth()/2
	cy = GraphicsHeight()/2

	Color 12,13,16

	Rect 0,0,GraphicsWidth(),18,True
	Rect 0,GraphicsHeight()-18,GraphicsWidth(),18,True

	Color 18,19,24

	Rect 10,10,610,26,True

	Color 255,255,255

	Text 20,17,"WASD: Hareket   SHIFT: Kos   SPACE: Goz Kirp   F11: Tam Ekran"

	Color 255,255,255

	Rect cx-1,cy-6,2,5,True
	Rect cx-1,cy+2,2,5,True
	Rect cx-6,cy-1,5,2,True
	Rect cx+2,cy-1,5,2,True

	If SCPState = STATE_NORMAL Then

		If IsNearCrate() = 1 Then

			Color 255,220,80

			Text GraphicsWidth()/2-130,GraphicsHeight()-130,"[E] SCP-173'u AL"

		EndIf

	EndIf

	If SCPState = STATE_PICKUP Then

		Color 255,220,80

		Text GraphicsWidth()/2-110,GraphicsHeight()-130,"SCP-173 aliniyor..."

	EndIf

	If SCPState = STATE_CARRY Then

		Color 255,220,80

		Text GraphicsWidth()/2-100,GraphicsHeight()-130,"Makineye gotur"

		If IsNearMachine() = 1 Then

			Color 100,255,120

			Text GraphicsWidth()/2-180,GraphicsHeight()-95,"[E] MAKINEYE YERLESTIR"

		EndIf

	EndIf

	If SCPState = STATE_PLACE Then

		Color 255,220,80

		Text GraphicsWidth()/2-150,GraphicsHeight()-130,"SCP-173 yerlesiyor..."

	EndIf

	If SCPState = STATE_MACHINE_READY Then

		If IsNearMachine() = 1 Then

			Color 100,255,120

			Text GraphicsWidth()/2-170,GraphicsHeight()-130,"[E] MAKINEYI CALISTIR"

		Else

			Color 255,220,80

			Text GraphicsWidth()/2-90,GraphicsHeight()-130,"Makine hazir"

		EndIf

	EndIf

	If SCPState = STATE_ACTIVATE Then

		Color 255,120,60

		Text GraphicsWidth()/2-190,GraphicsHeight()-130,"SISTEM CALISTIRILIYOR..."

	EndIf

	If SCPState = STATE_ACTIVE Then

		pulse# = 200+Sin(MilliSecs() Mod 360)*55

		Color 255,pulse#,pulse#

		Text 20,55,"UYARI: SCP-173 AKTIF"

	EndIf

	If SCPState = STATE_ATTACK Then

		Color 255,40,40

		Text GraphicsWidth()/2-75,GraphicsHeight()/2,"YAKALANDIN"

	EndIf

	If PlayerDead = 1 Then

		Color 255,0,0

		Text GraphicsWidth()/2-140,GraphicsHeight()/2+50,"SCP-173 seni yakaladi."

	EndIf

	If BlinkActive = 1 Then

		Color 0,0,0

		Rect 0,0,GraphicsWidth(),GraphicsHeight(),True

	EndIf

End Function

; ============================================================
; RESET GAME
; ============================================================

Function ResetGame()

	EntityParent SCP173,0

	PositionEntity Player,0,PLAYER_HEIGHT,-8

	CamYaw# = 0
	CamPitch# = 0

	RotateEntity Player,0,CamYaw#,0
	RotateEntity Camera,CamPitch#,0,0

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	SCPAlive = 0
	SCPPicked = 0
	SCPPlaced = 0

	SCPState = STATE_NORMAL
	SCPAnimTimer = 0

	PositionEntity SCP173,-2.6,SCP_GROUND_Y#,.7
	RotateEntity SCP173,-90,0,0

	ApplySCPTexture SCPUnpaintedTexture

	ForceSCPScale()

	SCPPathCount = 0
	SCPPathIndex = 0
	SCPPathTimer = 0

	BlinkSCPStartX# = EntityX(SCP173)
	BlinkSCPStartZ# = EntityZ(SCP173)

	BlinkSCPDistance# = 0

	BlinkActive = 0

	MachineReady = 0
	MachineRunning = 0
	MachineAnimTimer = 0

	EntityColor MachineScreen,30,150,200

	HideEntity AlarmLight

	StopChannel FootstepChannel

	FootstepMode = 0

	StopChannel MusicChannel

	MusicChannel = PlaySound(OstSound)

	CurrentMusic = 0

	PlayerDead = 0
	DeathTime = 0

End Function

; ============================================================
; FULLSCREEN
; ============================================================

Function ToggleFullscreen()

	Local oldX#
	Local oldY#
	Local oldZ#

	Local oldYaw#
	Local oldPitch#

	Local oldState
	Local oldPicked
	Local oldPlaced
	Local oldAlive
	Local oldReady
	Local oldRunning
	Local oldDead

	oldX# = EntityX(Player)
	oldY# = EntityY(Player)
	oldZ# = EntityZ(Player)

	oldYaw# = CamYaw#
	oldPitch# = CamPitch#

	oldState = SCPState

	oldPicked = SCPPicked
	oldPlaced = SCPPlaced
	oldAlive = SCPAlive

	oldReady = MachineReady
	oldRunning = MachineRunning

	oldDead = PlayerDead

	If FULLSCREEN = 0 Then
		FULLSCREEN = 1
	Else
		FULLSCREEN = 0
	EndIf

	ClearWorld

	ObsCount = 0

	EndGraphics

	Graphics3D SCREEN_W,SCREEN_H,SCREEN_DEPTH,FULLSCREEN

	SetBuffer BackBuffer()

	SetupAtmosphere()

	CreateBuilding()
	CreateSideRoom()
	CreateRoomDetails()

	CreateDoor 0,-4
	CreateDoor 1,4
	CreateDoor 2,12

	CreateCrate()
	CreateMachine()
	CreateOutdoor()

	CreatePlayer()
	CreateSCP()

	PositionEntity Player,oldX#,PLAYER_HEIGHT,oldZ#

	CamYaw# = oldYaw#
	CamPitch# = oldPitch#

	RotateEntity Player,0,CamYaw#,0
	RotateEntity Camera,CamPitch#,0,0

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	SCPPicked = oldPicked
	SCPPlaced = oldPlaced
	SCPAlive = oldAlive

	MachineReady = oldReady
	MachineRunning = oldRunning

	SCPState = oldState
	PlayerDead = oldDead

	If oldState = STATE_CARRY Then

		EntityParent SCP173,Player

		PositionEntity SCP173,0,.15,-1.1
		RotateEntity SCP173,0,0,0

	Else

		If oldState = STATE_MACHINE_READY Then

			PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
			RotateEntity SCP173,-90,0,0

		Else

			If oldState = STATE_ACTIVATE Then

				PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70
				RotateEntity SCP173,-90,0,0

			Else

				If oldState = STATE_ACTIVE Or oldState = STATE_ATTACK Then

					PositionEntity SCP173,2.8,SCP_GROUND_Y#,.70

					FaceSCPToPlayer()

				Else

					PositionEntity SCP173,-2.6,SCP_GROUND_Y#,.7
					RotateEntity SCP173,-90,0,0

				EndIf

			EndIf

		EndIf

	EndIf

	If SCPAlive = 1 Then
		ApplySCPTexture SCPTexture
	Else
		ApplySCPTexture SCPUnpaintedTexture
	EndIf

	ForceSCPScale()

	If MachineReady = 1 Then
		EntityColor MachineScreen,30,220,80
	EndIf

	SCPPathCount = 0
	SCPPathIndex = 0
	SCPPathTimer = 0

	BlinkSCPStartX# = EntityX(SCP173)
	BlinkSCPStartZ# = EntityZ(SCP173)

	BlinkSCPDistance# = 0

End Function

; ============================================================
; INITIAL WORLD
; ============================================================

ResetObstacles()

SetupAtmosphere()

CreateBuilding()
CreateSideRoom()
CreateRoomDetails()

CreateDoor 0,-4
CreateDoor 1,4
CreateDoor 2,12

CreateCrate()
CreateMachine()
CreateOutdoor()

CreatePlayer()
CreateSCP()

MusicChannel = PlaySound(OstSound)
CurrentMusic = 0

LastFrameTime = MilliSecs()

; ============================================================
; MAIN LOOP
; ============================================================

While KeyHit(1) = 0

	MainNow = MilliSecs()

	MainFrameMS = MainNow-LastFrameTime

	LastFrameTime = MainNow

	If MainFrameMS < 0 Then
		MainFrameMS = 0
	EndIf

	If MainFrameMS > 100 Then
		MainFrameMS = 100
	EndIf

	MainDT# = Float(MainFrameMS)/1000.0

	; ========================================================
	; E
	; ========================================================

	EPressed = KeyHit(18)

	; ========================================================
	; F11
	; ========================================================

	If KeyDown(87) Then

		If LastF11 = 0 Then
			ToggleFullscreen()
		EndIf

		LastF11 = 1

	Else

		LastF11 = 0

	EndIf

	; ========================================================
	; MOUSE
	; ========================================================

	UpdateMouse()

	; ========================================================
	; BLINK
	; ========================================================

	If KeyDown(57) Then

		If BlinkActive = 0 Then
			StartBlink()
		EndIf

	EndIf

	UpdateBlink()

	; ========================================================
	; PLAYER
	; ========================================================

	UpdatePlayer MainDT#

	; ========================================================
	; DOORS
	; ========================================================

	UpdateDoors()

	; ========================================================
	; ATMOSPHERE
	; ========================================================

	UpdateCreepyLight()

	; ========================================================
	; MUSIC
	; ========================================================

	UpdateMusicLoop()

	; ========================================================
	; PICKUP
	; ========================================================

	TryPickup()
	UpdatePickup()

	; ========================================================
	; CARRY
	; ========================================================

	UpdateCarry()

	; ========================================================
	; PLACE
	; ========================================================

	UpdatePlace()

	; ========================================================
	; MACHINE
	; ========================================================

	TryMachine()
	UpdateMachine()

	; ========================================================
	; SCP
	; ========================================================

	UpdateSCP MainDT#

	; ========================================================
	; ATTACK
	; ========================================================

	CheckSCPAttack()

	; ========================================================
	; RESET
	; ========================================================

	If PlayerDead = 1 Then

		If MilliSecs()-DeathTime > RESTART_DELAY Then
			ResetGame()
		EndIf

	EndIf

	; ========================================================
	; RENDER
	; ========================================================

	Cls

	RenderWorld

	DrawHUD

	Flip

Wend

End