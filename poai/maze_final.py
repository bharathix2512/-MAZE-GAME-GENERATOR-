import pygame, random, time
from collections import deque

pygame.init()

# ---------------- WINDOW ----------------
WIDTH, HEIGHT = 1000, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Maze Game Generator")

# ---------------- COLORS ----------------
BG = (20,20,40)
WHITE=(230,230,230)
BLACK=(0,0,0)
BLUE=(0,170,255)
GREEN=(0,200,0)
RED=(220,60,60)
YELLOW=(255,200,0)
BTN=(60,60,100)

# ---------------- FONTS ----------------
title_font = pygame.font.SysFont("arial",36,bold=True)
font = pygame.font.SysFont("arial",22)

# ---------------- MAZE CONFIG ----------------
ROWS,COLS=15,15
CELL=25
MX,MY=450,80
DIRS=[(0,1),(1,0),(0,-1),(-1,0)]

maze=None
path=None
player=[0,0]
goal=(ROWS-1,COLS-1)
generated=False
state="MENU"
timer=0

# ---------------- BUTTON ----------------
def button(r,text):
    pygame.draw.rect(screen,BTN,r,border_radius=6)
    t=font.render(text,True,WHITE)
    screen.blit(t,(r.x+20,r.y+10))

# ---------------- MESSAGE ----------------
def message(text):
    overlay=pygame.Surface((WIDTH,HEIGHT))
    overlay.set_alpha(180)
    overlay.fill((0,0,0))
    screen.blit(overlay,(0,0))
    t=font.render(text,True,WHITE)
    screen.blit(t,(WIDTH//2-150,HEIGHT//2))
    pygame.display.flip()
    time.sleep(1.5)

# ---------------- MAZE GENERATE ----------------
def generate_maze():
    m=[[1]*COLS for _ in range(ROWS)]
    stack=[(0,0)]
    m[0][0]=0
    while stack:
        x,y=stack[-1]
        nbr=[]
        for dx,dy in DIRS:
            nx,ny=x+dx*2,y+dy*2
            if 0<=nx<ROWS and 0<=ny<COLS and m[nx][ny]==1:
                nbr.append((nx,ny))
        if nbr:
            nx,ny=random.choice(nbr)
            m[x+(nx-x)//2][y+(ny-y)//2]=0
            m[nx][ny]=0
            stack.append((nx,ny))
        else:
            stack.pop()
    return m

# ---------------- SOLVER BFS ----------------
def solve_maze():
    q=deque([(0,0)])
    prev={(0,0):None}
    while q:
        x,y=q.popleft()
        if (x,y)==goal: break
        for dx,dy in DIRS:
            nx,ny=x+dx,y+dy
            if 0<=nx<ROWS and 0<=ny<COLS and maze[nx][ny]==0 and (nx,ny) not in prev:
                prev[(nx,ny)]=(x,y)
                q.append((nx,ny))
    p=[]
    cur=goal
    while cur:
        p.append(cur)
        cur=prev.get(cur)
    return p[::-1]

# ---------------- DRAW MAZE ----------------
def draw_maze(show_path=False):
    for r in range(ROWS):
        for c in range(COLS):
            col=WHITE if maze[r][c]==0 else BLACK
            pygame.draw.rect(screen,col,(MX+c*CELL,MY+r*CELL,CELL-1,CELL-1))

    pygame.draw.rect(screen,GREEN,(MX,MY,CELL-1,CELL-1))
    pygame.draw.rect(screen,RED,(MX+goal[1]*CELL,MY+goal[0]*CELL,CELL-1,CELL-1))

    if show_path:
        for x,y in path:
            pygame.draw.rect(screen,BLUE,(MX+y*CELL,MY+x*CELL,CELL-1,CELL-1))

    pygame.draw.rect(screen,YELLOW,(MX+player[1]*CELL,MY+player[0]*CELL,CELL-1,CELL-1))

# ---------------- MAIN LOOP ----------------
clock=pygame.time.Clock()
running=True

while running:
    screen.fill(BG)

    for e in pygame.event.get():
        if e.type==pygame.QUIT:
            running=False

        if e.type==pygame.MOUSEBUTTONDOWN:
            mx,my=e.pos

            if gen_btn.collidepoint(mx,my):
                maze=generate_maze()
                path=solve_maze()
                player=[0,0]
                generated=True
                timer=time.time()
                state="GENERATE"

            if solve_btn.collidepoint(mx,my):
                if not generated:
                    message("Generate Maze First")
                else:
                    state="PLAY"

            if sol_btn.collidepoint(mx,my):
                if not generated:
                    message("Generate Maze First")
                else:
                    timer=time.time()
                    state="SOLUTION"

            if back_btn.collidepoint(mx,my):
                running=False

        if e.type==pygame.KEYDOWN and state=="PLAY":
            dx=dy=0
            if e.key==pygame.K_UP: dx=-1
            if e.key==pygame.K_DOWN: dx=1
            if e.key==pygame.K_LEFT: dy=-1
            if e.key==pygame.K_RIGHT: dy=1
            nx,ny=player[0]+dx,player[1]+dy
            if 0<=nx<ROWS and 0<=ny<COLS and maze[nx][ny]==0:
                player=[nx,ny]

    # ---------------- UI ----------------
    screen.blit(title_font.render("MAZE GAME GENERATOR",True,BLUE),(40,40))

    gen_btn=pygame.Rect(80,150,260,45)
    solve_btn=pygame.Rect(80,210,260,45)
    sol_btn=pygame.Rect(80,270,260,45)
    back_btn=pygame.Rect(140,340,140,40)

    button(gen_btn,"Generate Maze")
    button(solve_btn,"Solve")
    button(sol_btn,"Solution")
    button(back_btn,"Back")

    if maze:
        if state=="SOLUTION":
            draw_maze(True)
            if time.time()-timer>5:
                state="MENU"

        elif state=="GENERATE":
            draw_maze()
            if time.time()-timer>5:
                state="MENU"

        elif state=="PLAY":
            draw_maze()
            if tuple(player)==goal:
                message("YOU WIN!")
                running=False

        else:
            draw_maze()

    pygame.display.flip()
    clock.tick(30)

pygame.quit()
