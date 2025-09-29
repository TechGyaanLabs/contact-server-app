# Deployment Guide for Render.com

## Prerequisites
- GitHub repository with your code
- Render.com account
- PostgreSQL database (Render.com provides this)

## Deployment Steps

### 1. Prepare Your Repository
Make sure your repository contains:
- `Dockerfile`
- `.dockerignore`
- `render.yaml` (optional, for easy setup)
- `src/main/resources/application-prod.properties`

### 2. Deploy to Render.com

#### Option A: Using render.yaml (Recommended)
1. Push your code to GitHub
2. Go to Render.com dashboard
3. Click "New +" → "Blueprint"
4. Connect your GitHub repository
5. Render will automatically detect `render.yaml` and create the service

#### Option B: Manual Setup
1. Go to Render.com dashboard
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Name**: contact-server-app
   - **Environment**: Docker
   - **Dockerfile Path**: ./Dockerfile
   - **Plan**: Free (or paid for production)
   - **Region**: Choose closest to your users

### 3. Environment Variables
Set these in Render.com dashboard:

```
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=<provided by Render database>
SPRING_DATASOURCE_USERNAME=<provided by Render database>
SPRING_DATASOURCE_PASSWORD=<provided by Render database>
```

### 4. Database Setup
1. Create a PostgreSQL database in Render.com
2. Render will provide connection details
3. Your app will automatically run Liquibase migrations

### 5. Health Check
Your app includes a health check endpoint:
```
GET https://your-app-name.onrender.com/api/v1/contacts/health
```

## API Endpoints

### Base URL
```
https://your-app-name.onrender.com/api/v1/contacts
```

### Available Endpoints
- `POST /api/v1/contacts` - Create single contact
- `POST /api/v1/contacts/batch` - Create multiple contacts
- `GET /api/v1/contacts` - Get all contacts
- `GET /api/v1/contacts/{id}` - Get contact by ID
- `PUT /api/v1/contacts/{id}` - Update contact
- `DELETE /api/v1/contacts/{id}` - Delete contact
- `GET /api/v1/contacts/search?q={term}` - Search contacts
- `GET /api/v1/contacts/health` - Health check

## Testing Your Deployment

### Create a contact:
```bash
curl -X POST https://your-app-name.onrender.com/api/v1/contacts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "mobile": "1234567890",
    "dob": "1990-01-01"
  }'
```

### Get all contacts:
```bash
curl https://your-app-name.onrender.com/api/v1/contacts
```

### Search contacts:
```bash
curl "https://your-app-name.onrender.com/api/v1/contacts/search?q=john"
```

## Troubleshooting

### Common Issues:
1. **App not starting**: Check logs in Render dashboard
2. **Database connection**: Verify environment variables
3. **CORS issues**: Check CORS configuration
4. **Health check failing**: Ensure health endpoint is accessible

### Logs:
- View logs in Render.com dashboard
- Check application logs for errors
- Verify database connection

### Performance:
- Free tier has limitations (sleeps after inactivity)
- Consider paid tier for production use
- Monitor resource usage

## Security Notes

1. **Environment Variables**: Never commit sensitive data
2. **CORS**: Configure allowed origins for production
3. **Database**: Use strong passwords
4. **HTTPS**: Render provides HTTPS automatically

## Monitoring

- Use Render.com dashboard for monitoring
- Check health endpoint regularly
- Monitor database connections
- Set up alerts for failures
