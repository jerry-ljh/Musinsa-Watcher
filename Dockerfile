# build stage
FROM node:lts-alpine as build-stage

# production stage
FROM nginx:stable-alpine as production-stage
COPY  nginx.conf /etc/nginx/conf.d/default.conf
ADD /frontend/dist /frontend/dist/
ADD /frontend/dist /frontend/mobile/dist/
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]